import {IncomingMessage} from "http";
import * as jwt from 'jsonwebtoken';

import {AuthenticateOptions, AuthenticatePayload} from "./common";
import {UnsupportedGrantTypeError, InvalidGrantTypeError} from "./errors";

export const DEFAULT_TOKEN_LEEWAY = 60*10;
const DEFAULT_TOKEN_EXPIRY = 24*60*60;
const CLIENT_CREDENTIALS_GRANT_TYPE = "client_credentials";
const REFRESH_TOKEN_GRANT_TYPE = "refresh_token";

export interface TokenWithExpiry {
  token: string;
  expires_in: number;
}

export interface RefreshToken {
  token: string;
}

export interface AuthenticationResponse {
  access_token: string | TokenWithExpiry;
  token_type: string;
  expires_in: number;
  refresh_token: string;
}

export default class Authenticator {
  constructor(
    private appId: string,
    private appKeyId: string,
    private appKeySecret: string,

    //Customise token expiry and leeway
    private tokenExpiry?: number,
    private tokenLeeway?: number
  ) {
    if(!this.tokenExpiry) { this.tokenExpiry = DEFAULT_TOKEN_EXPIRY; }
    if(!this.tokenLeeway) { this.tokenLeeway = DEFAULT_TOKEN_LEEWAY; }
  }

  authenticate(authenticatePayload: AuthenticatePayload, options: AuthenticateOptions): AuthenticationResponse {
    let grantType = authenticatePayload["grant_type"];

    switch (grantType) {
      case CLIENT_CREDENTIALS_GRANT_TYPE:
        return this.authenticateWithClientCredentials(options);
      case REFRESH_TOKEN_GRANT_TYPE:
        let oldRefreshToken = authenticatePayload[REFRESH_TOKEN_GRANT_TYPE];
        return this.authenticateWithRefreshToken(oldRefreshToken, options);
      default:
        throw new UnsupportedGrantTypeError(`Requested type: "${grantType}" is not supported`);
    }
  }

  private authenticateWithClientCredentials(options: AuthenticateOptions): AuthenticationResponse {
    let {token} = this.generateAccessToken(options);
    let refreshToken = this.generateRefreshToken(options);

    return {
      access_token: token,
      token_type: "bearer",
      expires_in: this.tokenExpiry,
      refresh_token: refreshToken.token,
    };
  }

  private authenticateWithRefreshToken(oldRefreshToken: string, options: AuthenticateOptions): AuthenticationResponse {
      let decoded: any;

      try {
        decoded = jwt.verify(oldRefreshToken, this.appKeySecret, {
          issuer: `keys/${this.appKeyId}`,
          clockTolerance: this.tokenLeeway,
        });
      } catch (e) {
        let description: string = (e instanceof jwt.TokenExpiredError) ? "refresh token has expired" : "refresh token is invalid";
        throw new InvalidGrantTypeError(description);
      }

      if (decoded.refresh !== true) {
        throw new InvalidGrantTypeError("refresh token does not have a refresh claim");
      }

      if (options.userId !== decoded.sub) {
        throw new InvalidGrantTypeError("refresh token has an invalid user id");
      }

      let newAccessToken = this.generateAccessToken(options);
      let newRefreshToken = this.generateRefreshToken(options);

      return {
        access_token: newAccessToken,
        token_type: "bearer",
        expires_in: this.tokenExpiry,
        refresh_token: newRefreshToken.token,
      };
  }

  generateAccessToken(options: AuthenticateOptions): TokenWithExpiry {
    let now = Math.floor(Date.now() / 1000);

    let claims = {
      app: this.appId,
      iss: `api_keys/${this.appKeyId}`,
      iat: now - this.tokenLeeway,
      exp: now + this.tokenExpiry - this.tokenLeeway,
      sub: options.userId,
      su: options.su,
      ...options.serviceClaims
    };

    return {
      token: jwt.sign(claims, this.appKeySecret),
      expires_in: this.tokenExpiry,
    };
  }

  private generateRefreshToken(options: AuthenticateOptions): RefreshToken {
    let now = Math.floor(Date.now() / 1000);

    let claims = {
      app: this.appId,
      iss: `api_keys/${this.appKeyId}`,
      iat: now - this.tokenLeeway,
      refresh: true,
      sub: options.userId,
    };

    return {
      token: jwt.sign(claims, this.appKeySecret),
    };
  }
}

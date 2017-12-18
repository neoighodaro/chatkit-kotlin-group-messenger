import {IncomingMessage} from "http";

export type Headers = {
  [key: string]: string | string[];
};

export class ErrorResponse {
  constructor(
    public readonly statusCode: number,
    public readonly headers: Headers,
    public readonly error_type: string,
    public readonly error_description: string,
    public readonly error_uri?: string,
  ) {}
}

export interface RequestOptions {
  method: string;
  path: string;
  jwt?: string;
  headers?: Headers;
  body?: any;
  qs?: object;
}

export interface AuthenticateOptions {
  userId?: string;
  serviceClaims?: any;
  su?: boolean
}

export interface AuthenticatePayload {
  grant_type?: string;
  refresh_token?: string;
}

export interface IncomingMessageWithBody extends IncomingMessage {
  body?: any;
}

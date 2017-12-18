export class UnsupportedGrantTypeError extends Error {
  constructor(
    public readonly message: string) {
    super(message);
    this.name = "UnsupportedGrantTypeError";
  }
}

export class InvalidGrantTypeError extends Error {
  constructor(
    public readonly message: string) {
    super(message);
    this.name = "InvalidGrantTypeError";
  }
}
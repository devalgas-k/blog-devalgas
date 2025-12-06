export interface IHeaders {
  id?: number;
  logoHeadersContentType?: string | null;
  logoHeaders?: string | null;
}

export class Headers implements IHeaders {
  constructor(
    public id?: number,
    public logoHeadersContentType?: string | null,
    public logoHeaders?: string | null,
  ) {}
}

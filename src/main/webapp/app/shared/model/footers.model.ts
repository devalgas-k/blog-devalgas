export interface IFooters {
  id?: number;
  logoFootersContentType?: string | null;
  logoFooters?: string | null;
}

export class Footers implements IFooters {
  constructor(
    public id?: number,
    public logoFootersContentType?: string | null,
    public logoFooters?: string | null,
  ) {}
}

export interface ISubject {
  id?: number;
  titleFr?: string;
  titleEn?: string;
}

export class Subject implements ISubject {
  constructor(
    public id?: number,
    public titleFr?: string,
    public titleEn?: string,
  ) {}
}

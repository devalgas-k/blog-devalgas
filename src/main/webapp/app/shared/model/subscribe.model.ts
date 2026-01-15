export interface ISubscribe {
  id?: number;
  email?: string;
  langKey?: string | null;
  countryKey?: string | null;
  date?: Date | null;
  recaptchaToken?: string | null;
}

export class Subscribe implements ISubscribe {
  constructor(
    public id?: number,
    public email?: string,
    public langKey?: string | null,
    public countryKey?: string | null,
    public date?: Date | null,
    public recaptchaToken?: string | null,
  ) {}
}

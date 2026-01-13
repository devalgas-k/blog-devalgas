import { type ISubject } from '@/shared/model/subject.model';

export interface IMessage {
  id?: number;
  name?: string;
  email?: string;
  phone?: string | null;
  message?: string;
  fileContentType?: string | null;
  file?: string | null;
  date?: Date | null;
  langKey?: string | null;
  countryKey?: string | null;
  subject?: ISubject;
}

export class Message implements IMessage {
  constructor(
    public id?: number,
    public name?: string,
    public email?: string,
    public phone?: string | null,
    public message?: string,
    public fileContentType?: string | null,
    public file?: string | null,
    public date?: Date | null,
    public langKey?: string | null,
    public countryKey?: string | null,
    public subject?: ISubject,
  ) {}
}

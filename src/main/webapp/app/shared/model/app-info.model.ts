import { type IHeaders } from '@/shared/model/headers.model';
import { type IFooters } from '@/shared/model/footers.model';

export interface IAppInfo {
  id?: number;
  keyInfo?: string | null;
  valueInfo?: string | null;
  headers?: IHeaders | null;
  footers?: IFooters | null;
}

export class AppInfo implements IAppInfo {
  constructor(
    public id?: number,
    public keyInfo?: string | null,
    public valueInfo?: string | null,
    public headers?: IHeaders | null,
    public footers?: IFooters | null,
  ) {}
}

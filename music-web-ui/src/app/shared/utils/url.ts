import { environment } from '../../../environments/environment';

const TRACK_BASE_URL = environment.fileApi + '/audios';
const COVER_BASE_URL = environment.fileApi + '/images/covers';
const AVATAR_BASE_URL = environment.fileApi + '/images/avatars';
export { TRACK_BASE_URL, COVER_BASE_URL, AVATAR_BASE_URL };

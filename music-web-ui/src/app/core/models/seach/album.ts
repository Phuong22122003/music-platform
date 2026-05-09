import { Track } from "../track";
import { TrackAndWave } from "../track_wave";

export interface Album{
    id:string;
    cover_image_path:string;
    tracks:TrackAndWave[];
    like:number;
    release_date:string;
}
import { Track } from "../track";
import { TrackAndWave } from "../track_wave";

export interface Playlist{
    id:string;
    cover_image_path:string;
    tracks:TrackAndWave[];
    like:number;
    created_date?:string;
}
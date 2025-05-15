export type SeatType = 'North'|'East'|'South'|'West';
export const Seat = ['North', 'East', 'South', 'West'];
export const Rank = ['2','3','4','5','6','7','8','9','t','j','q','k','a'];
export const Suit = ['c', 'd', 'h', 's'];
export const myIdKey = "myPlayerId";
export const myTableKey = "myTableId";
export const mySeatKey = "mySeatId";

export interface CardModel {
    suit: string;
	rank: string;
	value: number;
	used: number;
}
export interface VisibleCardsModel {
    dummy: string[];
	myhand: string[];
}
export interface PlayerModel {
	name: string;
	userId: string;
	seat: number
}
export interface TableModel {
	tableId: string;
	players: PlayerModel[];
}

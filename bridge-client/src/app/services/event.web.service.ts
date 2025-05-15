import { Injectable } from '@angular/core';  
import { HttpClient, HttpResponse, HttpErrorResponse} from '@angular/common/http';  
import { TableModel, CardModel, VisibleCardsModel} from '/home/nigel/Desktop/bridge-client/src/app/datamodules';  
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { environment } from '/home/nigel/Desktop/bridge-client/src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class EventWebService  
{ 
  url =  environment.http.apiUrl + '/bridge/v1';
  //url =  "assets/testhand.json";
  //url2 =  "assets/testtables.json";
  //url3 =  "assets/testtable.json";
  constructor(private http: HttpClient) { } 

  newGame(): Observable<HttpResponse<VisibleCardsModel>> {
    return this.http.get<VisibleCardsModel>(
      `${this.url}/newgame/{tableid}/status`, {observe: 'response', responseType: 'json'})
      .pipe(
        catchError(this.handleError)
      );
  }

  joinTable(data:TableModel): Observable<HttpResponse<TableModel>> {
    console.log(JSON.stringify(data));
    return this.http.put<TableModel>(
      `${this.url}/joinTable`, data, {observe: 'response', responseType: 'json'})
      .pipe(
         catchError(this.handleError)
    );
  }

  makeBid(bid:string, tableid:string, myid:string): Observable<HttpResponse<string>> {
    return this.http.get<string>(
      `${this.url}/makeBid/userid/${myid}/tableid/${tableid}/bid/${bid}`, {observe: 'response', responseType: 'json'})
      .pipe(
        catchError(this.handleError)
    );
  }
  
  playCard(card:string, tableid:string, myid:string): Observable<HttpResponse<string>> {
    return this.http.get<string>(
      `${this.url}/playCard/userid/${myid}/tableid/${tableid}/card/${card}`, {observe: 'response', responseType: 'json'})
      .pipe(
        catchError(this.handleError)
      );
  }

  getHand(tableid:string, myid:string): Observable<HttpResponse<string>> {
    return this.http.get<string>(
      `${this.url}/getHand/userid/${myid}/tableid/${tableid}`, {observe: 'response', responseType: 'json'})
      .pipe(
        catchError(this.handleError)
      );
  }

  getActiveTables(): Observable<HttpResponse<TableModel[]>> {
    return this.http.get<TableModel[]>(
      `${this.url}/getAvailableTables`, {observe: 'response', responseType: 'json'})
      .pipe(
        catchError(this.handleError)
      );
  }

  createTable(data:any): Observable<HttpResponse<TableModel>> {
    return this.http.post<TableModel>(
      `${this.url}/createTable`, data, {observe: 'response', responseType: 'json'})
      .pipe(
        catchError(this.handleError)
      );
  }
  
  handleError(error: HttpErrorResponse) {
    if (error.status === 0) {
      // A client-side or network error occurred. Handle it accordingly.
      console.error('An error occurred:', error.message);
    } else {
      // The backend returned an unsuccessful response code.
      // The response body may contain clues as to what went wrong.
      console.error(
        `Backend returned code ${error.status}, ` +
        `body was: ${error.error}`);
    }
    // Return an observable with a user-facing error message.
    return throwError(() => new Error(`Web service error: ${error.message} ${error.status}`))
  }
}   
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AdmissionResponse } from '../models/admission.model';

@Injectable({
  providedIn: 'root',
})
export class AdmissionService {
  private readonly apiUrl = 'http://localhost:8080/api/admissions';

  constructor(private http: HttpClient) {}

  processAdmissions(file: File): Observable<AdmissionResponse> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<AdmissionResponse>(this.apiUrl, formData);
  }
}

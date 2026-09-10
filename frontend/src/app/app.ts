import { Component, signal } from '@angular/core';
import { Upload } from './components/upload/upload';
import { AdmissionResponse } from './models/admission.model';
import { CommonModule } from '@angular/common';
import { Ranking } from './components/ranking/ranking';

@Component({
  selector: 'app-root',
  imports: [Upload, Ranking, CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  response: AdmissionResponse | null = null;

  onResultsReady(response: AdmissionResponse): void {
    this.response = response;
  }

  reset(): void {
    this.response = null;
  }
}

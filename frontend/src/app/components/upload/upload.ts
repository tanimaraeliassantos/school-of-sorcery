import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { AdmissionResponse } from '../../models/admission.model';
import { AdmissionService } from '../../services/admission.service';

@Component({
  selector: 'app-upload',
  imports: [CommonModule],
  templateUrl: './upload.html',
  styleUrl: './upload.scss',
})
export class Upload {
  @Output() resultsReady = new EventEmitter<AdmissionResponse>();

  loading = false;
  error: string | null = null;

  constructor(private admissionService: AdmissionService) {}

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;

    const file = input.files[0];
    this.loading = true;
    this.error = null;

    this.admissionService.processAdmissions(file).subscribe({
      next: (response) => {
        this.loading = false;
        this.resultsReady.emit(response);
      },
      error: (err) => {
        this.loading = false;
        this.error = "You've nonplussed it. Please check the fileformat.";
      },
    });
  }
}

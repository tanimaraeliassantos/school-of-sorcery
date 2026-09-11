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
  isDragging = false;

  constructor(private admissionService: AdmissionService) {}

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;
    this.processFile(input.files[0]);
  }

  private processFile(file: File): void {
    this.loading = true;
    this.error = null;

    this.admissionService.processAdmissions(file).subscribe({
      next: (response) => {
        this.loading = false;
        this.resultsReady.emit(response);
      },
      error: () => {
        this.loading = false;
        this.error = "You've nonplussed it. Please check the file format.";
      },
    });
  }
  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;

    const files = event.dataTransfer?.files;
    if (!files?.length) return;

    const file = files[0];
    if (!file.name.endsWith('.json')) {
      this.error = 'Please upload a JSON file.';
      return;
    }

    this.processFile(file);
  }
}

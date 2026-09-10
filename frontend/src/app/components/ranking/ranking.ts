import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AdmissionResponse, AdmissionResult } from '../../models/admission.model';

@Component({
  selector: 'app-ranking',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ranking.html',
  styleUrl: './ranking.scss',
})
export class Ranking implements OnInit {
  @Input() response!: AdmissionResponse;

  all: AdmissionResult[] = [];
  filtered: AdmissionResult[] = [];
  searchTerm = '';
  statusFilter: 'ALL' | 'ACCEPTED' | 'REJECTED' = 'ALL';
  sortColumn: 'position' | 'age' | 'score' | 'status' | 'house' | null = 'position';
  sortDirection: 'asc' | 'desc' = 'asc';

  ngOnInit(): void {
    const accepted = this.response.accepted
      .filter((r) => !r.invitedByHeadmaster)
      .sort((a, b) => (a.position ?? 0) - (b.position ?? 0));

    const invited = this.response.accepted.filter((r) => r.invitedByHeadmaster);

    const rejected = this.response.rejected.sort((a, b) => {
      if (a.position === null && b.position === null) return 0;
      if (a.position === null) return -1;
      if (b.position === null) return 1;
      return a.position - b.position;
    });

    this.all = [...invited, ...accepted, ...rejected];
    this.filtered = [...this.all];
  }

  applyFilters(): void {
    let result = this.all.filter((r) => {
      const matchesStatus = this.statusFilter === 'ALL' || r.status === this.statusFilter;
      const term = this.searchTerm.toLowerCase();
      const matchesSearch =
        !term ||
        r.application.firstName.toLowerCase().includes(term) ||
        r.application.familyName.toLowerCase().includes(term);
      return matchesStatus && matchesSearch;
    });

    if (this.sortColumn) {
      result = result.sort((a, b) => {
        let valA: any;
        let valB: any;

        switch (this.sortColumn) {
          case 'position':
            valA = a.position ?? -1;
            valB = b.position ?? -1;
            break;
          case 'age':
            valA = a.application.age;
            valB = b.application.age;
            break;
          case 'score':
            valA = a.score;
            valB = b.score;
            break;
          case 'status':
            valA = a.status;
            valB = b.status;
            break;
          case 'house':
            valA = a.house ?? '';
            valB = b.house ?? '';
            break;
        }

        if (valA < valB) return this.sortDirection === 'asc' ? -1 : 1;
        if (valA > valB) return this.sortDirection === 'asc' ? 1 : -1;
        return 0;
      });
    }

    this.filtered = result;
  }

  onSearchChange(): void {
    this.applyFilters();
  }

  onFilterChange(): void {
    this.applyFilters();
  }

  sortBy(column: 'position' | 'age' | 'score' | 'status' | 'house'): void {
    if (this.sortColumn === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortColumn = column;
      this.sortDirection = 'asc';
    }
    this.applyFilters();
  }

  getRejectionLabel(r: AdmissionResult): string {
    if (r.invitedByHeadmaster) return 'Invited';
    if (!r.rejectionReason) return '';
    const labels: Record<string, string> = {
      BANNED_FAMILY: 'Banned family',
      OUT_OF_AGE: 'Out of age range',
      UNACCEPTABLE_WEAKNESS: 'Unacceptable weakness',
      OUT_OF_DATE: 'Outside dates',
      NO_PLACE: 'No place - rank' + r.position,
    };
    return labels[r.rejectionReason] ?? r.rejectionReason;
  }

  getAcceptedCount(): number {
    return this.response.accepted.length;
  }
  get rejectedCount(): number {
    return this.response.rejected.length;
  }
}

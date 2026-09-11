import { CommonModule } from '@angular/common';
import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
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
  @Output() reset = new EventEmitter<void>();

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

  onReset(): void {
    this.reset.emit();
  }

  selectedResult: AdmissionResult | null = null;

  onRowClick(result: AdmissionResult): void {
    this.selectedResult = result;
  }

  onClosePanel(): void {
    this.selectedResult = null;
  }

  getScoreBreakdown(result: AdmissionResult): { label: string; value: number }[] {
    const app = result.application;
    return [
      { label: `Virtue — ${app.virtue}`, value: result.virtuePoints },
      { label: `Family — ${app.familyName}`, value: result.familyPoints },
      { label: `Weakness — ${app.weakness}`, value: result.weaknessPoints },
      { label: `Age — ${app.age}`, value: result.agePoints },
    ];
  }

  private getVirtuePoints(virtue: string): number {
    const map: Record<string, number> = {
      cunning: 12,
      wit: 10,
      courage: 8,
      loyalty: 8,
      curiosity: 6,
      ambition: 6,
      patience: 4,
      kindness: 4,
    };
    return map[virtue] ?? 0;
  }

  private getFamilyPoints(family: string): number {
    const map: Record<string, number> = { Nutwood: 15, Marlowe: 10, Yewbank: 5 };
    return map[family] ?? 0;
  }

  private getWeaknessPoints(weakness: string): number {
    const map: Record<string, number> = {
      shyness: -2,
      untidiness: -4,
      impatience: -6,
      laziness: -8,
      pride: -10,
    };
    return map[weakness] ?? 0;
  }

  private getAgePoints(age: number): number {
    if (age >= 11 && age <= 13) return 5;
    if (age >= 14 && age <= 15) return 2;
    return 0;
  }

  getHouseStats(): { name: string; count: number; percentage: number }[] {
    const houses = ['Lion', 'Serpent', 'Raven', 'Badger'];
    const total = this.response.accepted.length;

    return houses.map((house) => {
      const count = this.response.accepted.filter((r) => r.house === house).length;
      return {
        name: house,
        count,
        percentage: total > 0 ? Math.round((count / total) * 100) : 0,
      };
    });
  }

  getHouseScores(result: AdmissionResult): { name: string; score: number; won: boolean }[] {
    const app = result.application;
    const houses = [
      {
        name: 'Lion',
        virtue: { courage: 8, loyalty: 5 },
        weakness: { shyness: 4 },
        family: { Marlowe: 4 },
      },
      {
        name: 'Serpent',
        virtue: { ambition: 8, cunning: 5 },
        weakness: { pride: 4 },
        family: { Ferris: 4 },
      },
      {
        name: 'Raven',
        virtue: { wit: 8, curiosity: 5 },
        weakness: { untidiness: 4 },
        family: { Yewbank: 4 },
      },
      {
        name: 'Badger',
        virtue: { kindness: 8, patience: 5 },
        weakness: { laziness: 4 },
        family: { Nutwood: 4 },
      },
    ];

    const scores = houses.map((h) => {
      let score = 0;
      score += (h.virtue as unknown as Record<string, number>)[app.virtue] ?? 0;
      score += (h.weakness as unknown as Record<string, number>)[app.weakness] ?? 0;
      score += (h.family as unknown as Record<string, number>)[app.familyName] ?? 0;
      return { name: h.name, score, won: h.name === result.house };
    });

    const max = Math.max(...scores.map((s) => s.score));
    return scores;
  }

  getMaxHouseScore(result: AdmissionResult): number {
    return Math.max(...this.getHouseScores(result).map((s) => s.score), 1);
  }
}

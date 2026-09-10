export interface Application {
  id: string;
  firstName: string;
  familyName: string;
  age: number;
  virtue: string;
  weakness: string;
  applicationDate: string;
}

export type RejectionReason =
  'BANNED_FAMILY' | 'OUT_OF_AGE' | 'UNNACCEPTABLE_WEAKNESS' | 'OUT_OF_DATE' | 'NO_PLACE';

export interface AdmissionResult {
  application: Application;
  score: number;
  position: number | null;
  status: 'ACCEPTED' | 'REJECTED';
  rejectionReason: RejectionReason | null;
  rejectionDetail: string | null;
  house: string | null;
  invitedByHeadmaster: boolean;
}

export interface AdmissionResponse {
  accepted: AdmissionResult[];
  rejected: AdmissionResult[];
}

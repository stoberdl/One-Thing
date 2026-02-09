export type TimeBracket = "<15" | "15-30" | "30+";

export type Frequency =
  | "WEEKLY"
  | "BIWEEKLY"
  | "MONTHLY"
  | "QUARTERLY"
  | "BIANNUALLY"
  | "YEARLY";
  export interface Category {
    id: number;
    name: string;
    icon: string;
    color: string;
  }


  export interface Task {
    id: number;
    name: string;
    categoryId: number;
    timeBracket: TimeBracket;
    priority: number;
    frequency: Frequency;
    createdAt: string;           
    lastCompleted: string | null; 
    prevCompleted: string | null;
    parentId: number | null;     
  }
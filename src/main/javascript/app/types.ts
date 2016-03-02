export type TodoActionVerb = "TODO_ADD" | "TODO_TOGGLE";

export interface ITodoAction {
    type: TodoActionVerb;
    id?: number;
    text?: string;
    filter?: string;
    completed?: boolean;
}

export type VisibilityActionVerb = "VISIBILITY_FILTER_SET" ;

export type VisibilityStateType = "SHOW_ALL" | "SHOW_COMPLETED" | "SHOW_ACTIVE";

export interface IVisibilityAction {
    type: VisibilityActionVerb;
    filter: VisibilityStateType;
}


export interface ITodoState {
    id?: number;
    text?: string;
    completed?: boolean;
}


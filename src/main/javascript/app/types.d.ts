export type TodoActionVerb = "TODO_ADD"
    | "TODO_TOGGLE"
    | "TODO_RECEIVE"
    ;

export interface ITodoAction {
    type: TodoActionVerb;
    todos?: ITodoState[];
    id?: number;
    filter?: string;
    title?: string;
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
    title?: string;
    completed?: boolean;
    order?: number;
    url?: string;
}


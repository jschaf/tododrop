import { ITodoAction, VisibilityStateType, IVisibilityAction} from './types'

let nextTodoId: number = 0;

export function addTodo(text: string): ITodoAction {
    return {id: nextTodoId++, type: 'TODO_ADD',  text};
}

export function toggleTodo(id: number): ITodoAction {
    return {type: 'TODO_TOGGLE', id};
}

export function setVisibilityFilter(filter: VisibilityStateType): IVisibilityAction {
    return {
        filter: filter,
        type: 'VISIBILITY_FILTER_SET',
    };
}


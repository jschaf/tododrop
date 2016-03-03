import { addTodo, toggleTodo, setVisibilityFilter, receiveTodos, fetchTodos } from './actions'
import {ITodoState, VisibilityStateType, ITodoAction, IVisibilityAction} from './types';


import * as ReactDOM from 'react-dom';
import * as React from 'react';
import { Provider, connect } from 'react-redux';
import * as _ from 'lodash';

interface IAppState {
    todos: ITodoState[];
    visibilityFilter: VisibilityStateType;
}


import Reducer = Redux.Reducer;


// function checkStatus(response) {
//    if (response.status >= 200 && response.status < 300) {
//        return response;
//    } else {
//        let error = new Error(response.statusText);
//        error.response = response;
//        throw error;
//    }
// }

// Action Creators
// ===============




// Reducers
// ========

function todo(state: ITodoState, action: ITodoAction): ITodoState {
    switch (action.type) {
        case 'TODO_ADD':
            return {
                completed: false,
                id: action.id,
                title: action.title,
            };

        case 'TODO_TOGGLE':
            if (state.id !== action.id) {
                return state;
            } else {
                return _.assign(
                    {}, state, {completed: !state.completed}
                );
            }

        default:
            return state;
    }
}


function todos(state: ITodoState[] = [], action: ITodoAction): ITodoState[] {
    switch (action.type) {
        case 'TODO_ADD':
            return [
                ...state,
                todo(undefined, action),
            ];
        case 'TODO_TOGGLE':
            return state.map(t => todo(t, action));
        case 'TODO_RECEIVE':
            return action.todos;
        default:
            return state;
    }
}

function visibilityFilter(state: VisibilityStateType = 'SHOW_ALL',
                          action: IVisibilityAction): VisibilityStateType {
    switch (action.type) {
        case 'VISIBILITY_FILTER_SET':
            return action.filter;
        default:
            return state;
    }
}

import { createStore, combineReducers } from 'redux';


const todoApp: Reducer = combineReducers({
    todos,
    visibilityFilter,
});

function getVisibleTodos (todos: ITodoState[],
                          filter: VisibilityStateType): ITodoState[] {
    switch (filter) {
        case 'SHOW_ALL':
            return todos;
        case 'SHOW_COMPLETED':
            return todos.filter(t => t.completed);
        case 'SHOW_ACTIVE':
            return todos.filter(t => !t.completed);
        default:
            return todos;
    }
}

function Todo({onClick, completed, title}: {onClick: () => void, completed: boolean, title: string}): JSX.Element {
    return (
        <li
            onClick={onClick}
            style={{ textDecoration: completed ? 'line-through' : 'none' }}
        >
            {title}
        </li>
    );
}

interface ITodoListProps {
    todos?: ITodoState[];
    onTodoClick?: (todoId: number) => ITodoAction;
}

function TodoList ({todos = [], onTodoClick}: ITodoListProps): JSX.Element {
    return (
        <ul>
            {todos.map(todo =>
                <Todo
                    key={todo.id}
                    {...todo}
                    onClick={() => onTodoClick(todo.id)}
                />
            )}
        </ul>
    );
}

function mapStateToTodoListProps(state: IAppState): ITodoListProps {
   return {
       todos: getVisibleTodos(state.todos, state.visibilityFilter),
   };
}

function mapDispatchToTodoListProps(dispatch: Redux.Dispatch): ITodoListProps {
    return {
        onTodoClick: (id: number): any => dispatch(toggleTodo(id)),
    };
}

const VisibleTodoList: React.StatelessComponent<ITodoListProps> =
    connect(mapStateToTodoListProps, mapDispatchToTodoListProps)(TodoList);


function AddTodoSimple({ dispatch }: {dispatch: Redux.Dispatch}): JSX.Element {
    let input: HTMLInputElement;

    return (
        <div>
            <input ref={node => {input = node;}} />
            <button onClick={ () => {
                dispatch(addTodo(input.value));
                input.value = '';
                }}
            >
                Add Todo
            </button>
        </div>
    );
}

const AddTodo: React.StatelessComponent<any> = connect()(AddTodoSimple);

interface ILinkProps extends React.Props<any> {
    active?: boolean;
    filter?: VisibilityStateType;
    onClick?: () => any;
}

function Link({active, onClick, children}: ILinkProps): JSX.Element {
    if (active) {
        return <span>{children}</span>;
    }

    return (
        <a href='#' onClick={onClick}>{children}</a>
    );
}

function mapStateToLinkProps(state: IAppState, ownProps: ILinkProps): ILinkProps {
    return {
        active: ownProps.filter === state.visibilityFilter,
    };
}

function mapDispatchToLinkProps (dispatch: Redux.Dispatch, ownProps: ILinkProps): ILinkProps {
    return {
        onClick: (): any => dispatch(setVisibilityFilter(ownProps.filter)),
    };
}


const FilterLink: React.StatelessComponent<ILinkProps> =
    connect(mapStateToLinkProps, mapDispatchToLinkProps)(Link);

function FetchTodosLink({onClick, children}): JSX.Element {
    return (
        <a href="#" onClick={onClick}>{children}</a>
    );
}

function mapStateToFetchTodosProps(state: IAppState) {
    return {};
}

function mapDispatchToFetchTodosProps(dispatch: Redux.Dispatch) {
    return {
        onClick: (): any => fetchTodos(dispatch)
    }
}

const FetchLink: React.StatelessComponent<any> = connect(mapStateToFetchTodosProps,
    mapDispatchToFetchTodosProps)(FetchTodosLink);

function Footer(): JSX.Element {
    return (
        <p>Show:
        {' '} <FilterLink filter='SHOW_ALL'>All</FilterLink>
            {' '} <FilterLink filter='SHOW_ACTIVE'>Active</FilterLink>
            {' '} <FilterLink filter='SHOW_COMPLETED'>Completed</FilterLink>
            {' '} <FetchLink>Fetch Todos</FetchLink>
        </p>
    );
}

const TodoApp: () => JSX.Element =
    () => (
    <div>
        <AddTodo />
        <VisibleTodoList />
        <Footer />
    </div>
);

ReactDOM.render(
    <Provider store={createStore(todoApp)}>
        <TodoApp />
    </Provider>,
    document.getElementById('app')
);



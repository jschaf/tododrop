import * as ReactDOM from 'react-dom';
import * as React from 'react';
import { Provider } from 'react-redux';
import fetch from 'isomorphic-fetch';


const SERVER_URL = 'http://localhost:9090/api/';

//function checkStatus(response) {
//    if (response.status >= 200 && response.status < 300) {
//        return response;
//    } else {
//        let error = new Error(response.statusText);
//        error.response = response;
//        throw error;
//    }
//}

function parse(response) {
    return response.json();
}

// Action Creators
// ===============

export const RECEIVE_TODOS = 'RECEIVE_TODOS';

const receiveTodos = (json) => {
    return {
        type: RECEIVE_TODOS
    }
};

const fetchTodos = () => {
    return (dispatch) => {
        return fetch(SERVER_URL)
            .then(req => req.json)
            .then(json => dispatch(receiveTodos))
    };
};


let nextTodoId = 0;
const addTodo = (text) => {
    return {type: 'ADD_TODO', id: nextTodoId++, text};
};

const toggleTodo = (id) => {
    return {type: 'TOGGLE_TODO', id};
};

const setVisibilityFilter = (filter) => {
    return {
        type: 'SET_VISIBILITY_FILTER',
        filter
    };
};



// Reducers
// ========

const todo = (state, action) => {
    switch (action.type) {
        case 'ADD_TODO':
            return {
                id: action.id,
                text: action.text,
                completed: false
            };
        case 'TOGGLE_TODO':
            if (state.id !== action.id) {
                return state;
            } else {
                return Object.assign(
                    {}, state, {completed: !state.completed}
                );
            }
        default:
            return state;
    }
};


const todos = (state = [], action) => {
    switch (action.type) {
        case 'ADD_TODO':
            return [
                ...state,
                todo(undefined, action)
            ];
        case 'TOGGLE_TODO':
            return state.map(t => todo(t, action));
        default:
            return state;
    }
};

const visibilityFilter = (
    state = 'SHOW_ALL',
    action
) => {
    switch (action.type) {
        case 'SET_VISIBILITY_FILTER':
            return action.filter;
        default:
            return state;
    }
};

import { createStore, combineReducers } from 'redux';


const todoApp = combineReducers({
    todos,
    visibilityFilter
});




const getVisibleTodos = (
    todos,
        filter
) => {
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
};

const Todo = ({
    onClick,
    completed,
    text
}) => (
    <li
        onClick={onClick}
        style={{ textDecoration: completed ? 'line-through' : 'none' }}
    >
        {text}
    </li>
);

const TodoList = ({
    todos,
    onTodoClick
}) => (
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

const mapStateToTodoListProps = (state) => {
   return {
       todos: getVisibleTodos(state.todos, state.visibilityFilter)
   };
};

const mapDispatchToTodoListProps = (dispatch) => {
    return {
        onTodoClick: id => dispatch(toggleTodo(id))
    };
};

import { connect } from 'react-redux';

const VisibleTodoList = connect(mapStateToTodoListProps, mapDispatchToTodoListProps)(TodoList);



VisibleTodoList.contextTypes = {
    store: React.PropTypes.object
};

const AddTodoSimple = ({ dispatch }) => {
    let input;

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
};

const AddTodo = connect()(AddTodoSimple);

const Link = ({
    active,
    onClick,
    children
}) => {
    if (active) {
        return <span>{children}</span>;
    }

    return (
        <a href="#" onClick={onClick}>{children}</a>
    );
};

const mapStateToLinkProps = (
    state,
    ownProps
) => {
    return {
        active: ownProps.filter === state.visibilityFilter
    };
};

const mapDispatchToLinkProps = (
    dispatch,
    ownProps
) => {
    return {
        onClick: () => dispatch(setVisibilityFilter(ownProps.filter))
    };
};

const FilterLink = connect(mapStateToLinkProps, mapDispatchToLinkProps)(Link);


const Footer = () => (
    <p>Show:
        {' '} <FilterLink filter='SHOW_ALL'>All</FilterLink>
        {' '} <FilterLink filter='SHOW_ACTIVE'>Active</FilterLink>
        {' '} <FilterLink filter='SHOW_COMPLETED'>Completed</FilterLink>
    </p>
);

const TodoApp = () => (
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
    document.getElementById('root')
);



import { ReduceStore } from 'flux/utils';
import Immutable from 'immutable';
import AppDispatcher from '../dispatcher/AppDispatcher';

class HelloWorldStore extends ReduceStore {
  getInitialState() {
    return Immutable.Map({
      "title": "Title...", 
      "text": "Text..."
    }).toJS();
  }

  reduce(state, action) {
    switch (action.type) {
      default:
        return state;
    }
  }
}

export default new HelloWorldStore(AppDispatcher);

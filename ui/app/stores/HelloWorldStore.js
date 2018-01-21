import { ReduceStore } from 'flux/utils';
import Immutable from 'immutable';
import AppDispatcher from '../dispatcher/AppDispatcher';
import HelloWorldActions from './HelloWorldActions';
import SampleInput from '../models/SampleInput'

class HelloWorldStore extends ReduceStore {
  getInitialState() {
    return Immutable.Map({
      "sampleResult": undefined,
      "sampleInput": new SampleInput()
    });
  }

  reduce(state, action) {
    switch (action.type) {
      case HelloWorldActions.SAMPLE_INPUT_NAME_CHANGE: {
        const oldInput = state.get("sampleInput");
        const newInput = new SampleInput({
          name: action.newValue,
          age: oldInput.age
        });
        return state.set("sampleInput", newInput);
      }
      case HelloWorldActions.SAMPLE_INPUT_AGE_CHANGE: {
        const oldInput = state.get("sampleInput");
        const newInput = new SampleInput({
          name: oldInput.name,
          age: action.newValue
        });
        return state.set("sampleInput", newInput);
      }
      case HelloWorldActions.SAMPLE_RESPONSE_RECEIVED:
        return state.set("sampleResult", action.result);
      default:
        return state;
    }
  }
}

export default new HelloWorldStore(AppDispatcher);

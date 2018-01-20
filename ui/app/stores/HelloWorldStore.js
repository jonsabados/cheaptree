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
      case HelloWorldActions.SAMPLE_INPUT_A_CHANGE: {
        const oldInput = state.get("sampleInput");
        const newInput = new SampleInput({
          a: action.newValue,
          b: oldInput.b
        });
        return state.set("sampleInput", newInput);
      }
      case HelloWorldActions.SAMPLE_INPUT_B_CHANGE: {
        const oldInput = state.get("sampleInput");
        const newInput = new SampleInput({
          a: oldInput.a,
          b: action.newValue
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

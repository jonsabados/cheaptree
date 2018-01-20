import AppDispatcher from '../dispatcher/AppDispatcher'
import HelloWorldActions from '../stores/HelloWorldActions'

function aChanged(newValue) {
  AppDispatcher.dispatch({
    type: HelloWorldActions.SAMPLE_INPUT_A_CHANGE,
    newValue: newValue
  });
}

function bChanged(newValue) {
  AppDispatcher.dispatch({
    type: HelloWorldActions.SAMPLE_INPUT_B_CHANGE,
    newValue: newValue
  });
}

export {
  aChanged,
  bChanged
}
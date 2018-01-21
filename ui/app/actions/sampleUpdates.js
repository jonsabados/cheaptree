import AppDispatcher from '../dispatcher/AppDispatcher'
import HelloWorldActions from '../stores/HelloWorldActions'

function nameChanged(newValue) {
  AppDispatcher.dispatch({
    type: HelloWorldActions.SAMPLE_INPUT_NAME_CHANGE,
    newValue: newValue
  });
}

function ageChanged(newValue) {
  AppDispatcher.dispatch({
    type: HelloWorldActions.SAMPLE_INPUT_AGE_CHANGE,
    newValue: newValue
  });
}

export {
  nameChanged,
  ageChanged
}
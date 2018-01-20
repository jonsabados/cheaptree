import apiFetch from './apiFetch'
import AppDispatcher from '../dispatcher/AppDispatcher'
import HelloWorldActions from '../stores/HelloWorldActions'

function sampleFetch(input) {
  apiFetch("/hello", {
    method: "POST",
    body: JSON.stringify(input)
  }).then((res) => res.json())
    .then((json) => AppDispatcher.dispatch({
      type: HelloWorldActions.SAMPLE_RESPONSE_RECEIVED,
      result: json
    }))
    .catch((e) => {
      AppDispatcher.dispatch({
        type: HelloWorldActions.SAMPLE_RESPONSE_RECEIVED,
        result: e
      })
    })
}

export default sampleFetch;

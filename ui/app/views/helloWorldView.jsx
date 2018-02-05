import React from 'react';
import {nameChanged, ageChanged} from '../actions/sampleUpdates'
import sampleFetch from '../actions/sampleFetch'

function sampleResultContent(sampleResult) {
  if (sampleResult) {
    const content = JSON.stringify(sampleResult);
    return <pre>{content}</pre>
  } else {
    return <div>No result yet</div>;
  }
}

function helloWorldView(sampleInput, sampleResult, currentUser) {
  return <div>
    <p>Hello world!</p>
    <div>
      <label htmlFor="inputName">Name:</label>
      <input className="form-control"
             id="inputName"
             placeholder="bob"
             value={sampleInput.name}
             onChange={(event) => nameChanged(event.target.value)}/>
    </div>
    <div>
      <label htmlFor="inputAge">Age:</label>
      <input className="form-control"
             id="inputAge"
             placeholder="29"
             value={sampleInput.age}
             onChange={(event) => ageChanged(event.target.value)}/>
    </div>
    <button onClick={() => sampleFetch(sampleInput, currentUser)}>Send it!</button>
    {sampleResultContent(sampleResult)}
  </div>;
}

export default helloWorldView
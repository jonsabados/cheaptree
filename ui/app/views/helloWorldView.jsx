import React from 'react';
import {aChanged, bChanged} from '../actions/sampleUpdates'
import sampleFetch from '../actions/sampleFetch'

function sampleResultContent(sampleResult) {
  if (sampleResult) {
    const content = JSON.stringify(sampleResult);
    return <pre>{content}</pre>
  } else {
    return <div>No result yet</div>;
  }
}

function helloWorldView(sampleInput, sampleResult) {
  return <div>
    <p>Hello world!</p>
    <div>
      <label htmlFor="inputA">Value a:</label>
      <input className="form-control"
             id="inputA"
             placeholder="a"
             value={sampleInput.a}
             onChange={(event) => aChanged(event.target.value)}/>
    </div>
    <div>
      <label htmlFor="inputB">Value b:</label>
      <input className="form-control"
             id="inputB"
             placeholder="b"
             value={sampleInput.b}
             onChange={(event) => bChanged(event.target.value)}/>
    </div>
    <button onClick={() => sampleFetch(sampleInput)}>Send it!</button>
    {sampleResultContent(sampleResult)}
  </div>;
}

export default helloWorldView
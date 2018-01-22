import React from 'react';
import {nameChanged, ageChanged} from '../actions/sampleUpdates'
import sampleFetch from '../actions/sampleFetch'
import { GoogleLogin, GoogleLogout }from 'react-google-login';

const responseGoogle = (response) => {
  console.log(response);
  console.log(response.hg);
};

const logout = () => {
  console.log("Logged out");
};

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
    <button onClick={() => sampleFetch(sampleInput)}>Send it!</button>
    {sampleResultContent(sampleResult)}
    <GoogleLogin
      clientId="802743688838-6a65glf1ldg20mgl12va2c0j3pgkao6l.apps.googleusercontent.com"
      onSuccess={responseGoogle}
      onFailure={responseGoogle}
      isSignedIn={true}
    />
    <GoogleLogout
      buttonText="Logout"
      onLogoutSuccess={logout}
    />
  </div>;
}

export default helloWorldView
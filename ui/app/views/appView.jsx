import React from 'react';
import {GoogleLogin, GoogleLogout} from 'react-google-login';
import {googleLoggedIn, googleLoggedOut} from '../actions/googleAuth';
import HelloWorldContainer from '../components/HelloWorldContainer';

function appView(currentUser) {
  if (currentUser) {
    return <div>
      <HelloWorldContainer/>
      <div>
        <GoogleLogout buttonText="Logout" onLogoutSuccess={googleLoggedOut}/>
      </div>
    </div>;
  } else {
    return <div>
      <GoogleLogin
        clientId="802743688838-6a65glf1ldg20mgl12va2c0j3pgkao6l.apps.googleusercontent.com"
        onSuccess={googleLoggedIn}
        onFailure={googleLoggedIn}
        isSignedIn={true}
      />
    </div>;
  }
}

export default appView;
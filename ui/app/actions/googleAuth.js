import AppDispatcher from '../dispatcher/AppDispatcher';
import AppActions from '../stores/AppActions';

function googleLoggedIn(loginResult) {
  // TODO - set timer to refresh token every 45 minutes...
  AppDispatcher.dispatch({
    type     : AppActions.GOOGLE_LOGIN,
    authData : loginResult
  });
}

function googleLoggedOut() {
  AppDispatcher.dispatch({
    type : AppActions.GOOGLE_LOGOUT
  });
}

export {
  googleLoggedIn,
  googleLoggedOut
};
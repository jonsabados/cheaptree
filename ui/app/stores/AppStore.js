import {ReduceStore} from 'flux/utils';
import Immutable from 'immutable';
import AppDispatcher from '../dispatcher/AppDispatcher';
import AppActions from './AppActions';
import User from '../models/User';
import SateKeys from './StateKeys';

const AUTH_TYPE_GOOGLE = 'GOOGLE';

class AppStore extends ReduceStore {
  getInitialState() {
    return Immutable.Map({
      [SateKeys.CURRENT_USER] : undefined
    });
  }

  reduce(state, action) {
    switch (action.type) {
      case AppActions.GOOGLE_LOGIN: {
        const authData = action.authData;
        const user     = new User({
          authType        : AUTH_TYPE_GOOGLE,
          authorizerId    : authData.googleId,
          bearerToken     : authData.tokenId,
          tokenExpiration : authData.tokenObj.tokenExpiration
        });
        return state.set(SateKeys.CURRENT_USER, user);
      }
      case AppActions.GOOGLE_TOKEN_REFRESH: {
        const authData = action.authData;
        const oldUser  = state.get(USER_KEY);
        const user     = new User({
          authType        : AUTH_TYPE_GOOGLE,
          authorizerId    : oldUser.authorizerId,
          bearerToken     : authData.tokenId,
          tokenExpiration : authData.tokenObj.tokenExpiration
        });
        return state.set(SateKeys.CURRENT_USER, user);
      }
      case AppActions.GOOGLE_LOGOUT: {
        return state.set(SateKeys.CURRENT_USER, undefined);
      }
      default:
        return state;
    }
  }
}

export default new AppStore(AppDispatcher);

import Immutable from 'immutable';

const User = Immutable.Record({
  authType        : undefined,
  authorizerId    : undefined,
  bearerToken     : undefined,
  tokenExpiration : undefined
});

export default User;

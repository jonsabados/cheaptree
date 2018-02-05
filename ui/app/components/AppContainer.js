import React, {Component} from 'react';
import {Container} from 'flux/utils';
import AppStore from '../stores/AppStore';
import StateKeys from '../stores/StateKeys';
import appView from '../views/appView.jsx';

class AppContainer extends Component {
  static getStores() {
    return [AppStore];
  }

  static calculateState() {
    return {
      app : AppStore.getState()
    };
  }

  componentDidMount() {
  }

  render() {
    return appView(this.state.app.get(StateKeys.CURRENT_USER));
  }
}

export default Container.create(AppContainer);

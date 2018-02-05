import React, {Component} from 'react';
import {Container} from 'flux/utils';
import HelloWorldStore from '../stores/HelloWorldStore';
import AppStore from '../stores/AppStore';
import StateKeys from '../stores/StateKeys';
import helloWorldView from '../views/helloWorldView.jsx';

class HelloWorldContainer extends Component {
  static getStores() {
    return [HelloWorldStore, AppStore];
  }

  static calculateState() {
    return {
      sample : HelloWorldStore.getState(),
      app    : AppStore.getState()
    };
  }

  componentDidMount() {
  }

  render() {
    return helloWorldView(
      this.state.sample.get('sampleInput'),
      this.state.sample.get('sampleResult'),
      this.state.app.get(StateKeys.CURRENT_USER)
    );
  }
}

export default Container.create(HelloWorldContainer);

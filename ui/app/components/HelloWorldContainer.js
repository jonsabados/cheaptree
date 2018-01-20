import React, { Component } from 'react';
import { Container } from 'flux/utils';
import HelloWorldStore from '../stores/HelloWorldStore';
import helloWorldView from '../views/helloWorldView.jsx'

class HelloWorldContainer extends Component {
  static getStores() {
    return [HelloWorldStore];
  }

  static calculateState() {
    return {
      sample: HelloWorldStore.getState()
    };
  }

  componentDidMount() {
  }

  render() {
    return helloWorldView(this.state.sample.get("sampleInput"), this.state.sample.get("sampleResult"));
  }
}

export default Container.create(HelloWorldContainer);

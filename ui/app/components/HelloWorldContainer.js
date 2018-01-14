import React, { Component } from 'react';
import { Container } from 'flux/utils';
import { Link } from 'react-router-dom'
import HelloWorldStore from '../stores/HelloWorldStore';

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
    return (
      <div>
        <h1>Hello World</h1>
      </div>
    );
  }
}

export default Container.create(HelloWorldContainer);

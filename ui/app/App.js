import React from 'react';
import {render} from 'react-dom';
import {HashRouter as Router, Route} from 'react-router-dom';
import injectTapEventPlugin from 'react-tap-event-plugin';

import AppContainer from './components/AppContainer';

injectTapEventPlugin();

render(
  <Router>
    <div>
      <Route exact path="/" component={AppContainer}/>
    </div>
  </Router>,
  document.getElementById('root')
);

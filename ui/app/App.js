import React from "react";
import { render } from "react-dom";
import { HashRouter as Router, Route } from "react-router-dom";
import injectTapEventPlugin from "react-tap-event-plugin";

import HelloWorldContainer from "./components/HelloWorldContainer";

injectTapEventPlugin();

render(
    <Router>
      <div>
        <Route exact path="/" component={HelloWorldContainer} />
      </div>
    </Router>,
  document.getElementById("root")
)

import React from 'react'
import { Route } from 'react-router'

// 通用错误页面
import ErrorNotFoundPage from '@mdf/metaui-web/lib/components/errors/NotFound'
import DynamicView from '@mdf/metaui-web/lib/components/portal/DynamicView'
import BussinessMenu from './bussinessMenu';
import DebugScript from './DebugScript';
import RoutesMap from './config.route'
import axios from "axios";
import { HTTP_SERVICE_BASEURL } from '../config.env'

const renderRoutes = (routes) => {
  if (process.env.PREFIX) console.log('process.env.PREFIX', process.env.PREFIX);
  return routes.map((route, index) => {
    if (Array.isArray(route.routes) && route.routes.length > 0) {
      return <Route key={index} exact={route.exact} component={route.component} path={route.path}>
        {renderRoutes(route.routes)}
      </Route>
    } else {
      return <Route key={index} exact={route.exact} component={route.component} path={route.path} />
    }
  })
}


export default class Router extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      visible: false,
    }
  }
  componentDidMount () {
    const code = "hfihskfhkshfkjhkf";

    if (code) {
      axios.get(`${HTTP_SERVICE_BASEURL}/sso/auth/code`,
        {
          params: {
            yhtAccessTokenCipher: token,
            userId: yht_userid,
            tenantId: tenant_id,
          },
          withCredentials: true,
        }
      )
        .then((response) => {
          console.log(response);
          this.setState({ visible: true });
        })
        .catch((error) => {
          console.log(error);
          this.setState({ visible: true });
        })
    } else {
      this.setState({ visible: true })
    }

  }
  render() {
    return this.state.visible ?
      <Route>
        <Route path={process.env.__CLIENT__ ? (window._baseUrl || '') : (process.env.PREFIX || '')}>
          {renderRoutes(RoutesMap)}
          <Route path='menu' component={BussinessMenu} />
          <Route path='platform/:menuurl' component={DynamicView} />
          <Route path='meta' component={DebugScript}>
            <Route path=':billtype/:billno' component={DynamicView} />
            <Route path=':billtype/:billno/:billid' component={DynamicView} />
          </Route>
          <Route path='*' component={ErrorNotFoundPage} />
        </Route>
      </Route> : <div>loading</div>
  }
}



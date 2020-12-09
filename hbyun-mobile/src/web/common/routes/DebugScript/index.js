import React from 'react';
import {Modal, Form,Input} from 'antd';
if(process.env.__CLIENT__){
  require('./index.less')
}
class HomeControl extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      visible: false,
      value:''
    }
  }

  componentWillMount() {

  }

  componentDidMount() {
    document.addEventListener("keydown", this.onKeyDown)
  }
  setCookie = (cname, cvalue, exdays) => {
    var d = new Date();
    d.setTime(d.getTime() + (exdays * 60 * 1000));
    var expires = "expires=" + d.toUTCString();
    document.cookie = cname + "=" + cvalue + "; " + expires+"; path=/";
  }

  onKeyDown = (e) => {

    if (e.keyCode== 68 && e.shiftKey) {
      this.setState({
        visible: true
      })
    }
  };
  showModal = () => {
    this.setState({
      visible: true,
    });
  };

  handleOk = () => {
    let value = this.state.value;

    this.setCookie('debugcode',value,0.5);//value值为分钟
    this.setState({
      visible: false,
      value:''
    });
  };
  handleChange = (e) => {
    this.setState({
      value:e.target.value
    })
  }

  handleCancel = e => {
    this.setState({
      visible: false,
      value:''
    });
  };

  render() {
    return (
      <div className="wrapper-box">
        <Modal
          title="请输入调试句柄"
          visible={this.state.visible}
          onOk={this.handleOk}
          onCancel={this.handleCancel}
        >
          <Input value={this.state.value}  onChange={this.handleChange}  autofocus="autofocus" />
        </Modal>
        {this.props.children}
      </div>
    )
  }

}


export default HomeControl

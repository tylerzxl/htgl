import React, { Component } from 'react';
import Gumshoe from './lib/gumshoe.polyfills.js';
import SvgIcon from '@mdf/metaui-web/lib/components/common/SvgIcon';
import { parseContainer, parseControls } from '@mdf/metaui-web/lib/components/meta/util';

import Collapse from 'bee-collapse';
import 'bee-collapse/build/Collapse.css';

import './static/style.less';

export default class CollapseContainer extends Component {
    constructor(props) {
        super(props);
        this.state = {
            collapse: !!props.meta.label && !!props.meta.collapse
        };
        console.log('-------props--------', props, props.meta.label);
    }

    // 滚动容器 Dom
    get scroller() {
        return document.querySelector('.u-tabs-tabpane-active');
    }

    // 当前组件 Dom
    get container() {
        const { meta: { cGroupCode } } = this.props;
        return document.querySelector(`#${cGroupCode}`);
    }

    // anchor Dom
    get anchorContainer() {
        return document.querySelector('#anchor-container');
    }

    // isTop
    get isTop() {
        const { meta: { cGroupCode } } = this.props;
        const Dom = document.querySelector(`#${cGroupCode}`);
        if (Dom) {
            const className = Dom.className || '';
            const classList = className.split(/\s+/);
            if (classList &&
                classList.length) {
                if (!Dom.parentNode.closest(`.${classList[0]}`)) {
                    return true;
                }
            }
        }
        return false;
    }

    // 计算anchor的位置
    initAnchorPosition = (e) => {
        if (this.container) {
            if(this.anchorContainer) {
                if (!this.gumshoe) {
                    this.gumshoe = new Gumshoe('#anchor-container a', {
                        // Active classes
                        navClass: 'active', // applied to the nav list item
                        contentClass: 'active', // applied to the content
                        // Nested navigation
                        nested: false, // if true, add classes to parents of active link
                        nestedClass: 'active', // applied to the parent items
                        // Offset & reflow
                        offset: 0, // how far from the top of the page to activate a content area
                        reflow: false, // if true, listen for reflows
                        // Event support
                        events: true // if true, emit custom events
                    }, this.scroller);
                }
                const attr = this.container.getBoundingClientRect();
                const offsetTop = Math.abs(Math.min(50, Math.floor((this.container.clientHeight - (this.anchorContainer.clientHeight || 0)) / 2)));
                Object.assign(this.anchorContainer.style, {
                    top: `${attr.top <= 0 ? offsetTop : attr.top + offsetTop}px`,
                    display: (-attr.top > this.container.clientHeight) ? 'none' : 'block',
                    ...(attr.top <= 0 ? {
                        bottom: `${offsetTop}px`,
                        overflowY: `auto`
                    } : {
                        bottom: `auto`,
                        overflowY: `hidden`
                    })
                });
            }
        }
    }

    componentWillUnmount() {
        const { meta: { anchor } } = this.props;
        if(anchor) {
            if (this.scroller) {
                this.scroller.removeEventListener('scroll', this.initAnchorPosition);
            }
        }
    }

    componentDidMount() {
        const { meta: { anchor, containers }, viewModel } = this.props;
        if(anchor) {
            setTimeout(() => {
                this.initAnchorPosition();
                if (this.scroller) {
                    this.scroller.addEventListener('scroll', this.initAnchorPosition);
                }
            }, 300);
        }

        if (!this.props.meta.nid &&
            viewModel.getParams()['mode'] == 'add') {
            // 运行时，新增状态
            this.setState({
                collapse: false
            });
        } else {
            if (this.props.meta.label &&
                this.props.meta.collapse !== false) {
                this.setState({
                    collapse: true
                });
            } else {
                this.setState({
                    collapse: false
                });
            }
        }
    }

    // 自定义样式类
    gitCustormClass = (id) => {
        const { meta } = this.props;
        return (meta[id] || '').split(/\s+|,|;/);
    }

    // 自定义样式
    gitCustormStyle = (id) => {
        const { meta } = this.props;
        const style = {};
        (meta[id] || '').split(/\s*(,|;)\s*/).forEach(item => {
            const result = item.split(/\s*[:=]\s*/);
            if (result.length == 2) {
                style[result[0].trim()] = result[1].trim();
            }
        })
        return style;
    }

    render() {
        const {
            props: {
                viewModel,
                meta: { containers = [], controls, cGroupCode, nid, uitype, uititle, label, anchor },
                meta
            } = {},
        } = this,
            components = containers.map(v => parseContainer(v, viewModel)),
            isHasAnchor = anchor && containers.map(v => /^collapsecontainer$/.test(v.cControlType)).length > 0;

        let anchorContainerWidth = 0,
            fixedAnchorStyle = {},
            anchorTreeLoop;

        if(isHasAnchor) {
            const anchorContainerDefaultWidth = 150;
            const { anchorHorizontalAlign } = meta;
            const clientWidth = parseInt(this.anchorContainer && this.anchorContainer.clientWidth || 0) || anchorContainerDefaultWidth;

            anchorContainerWidth = Math.max(meta['anchorContainerWidth'] || anchorContainerDefaultWidth, anchorContainerDefaultWidth);
            fixedAnchorStyle['maxWidth'] = anchorContainerWidth;
            // fixedAnchorStyle['minWidth'] = anchorContainerDefaultWidth;

            anchorTreeLoop = ({ containers = [] } = {}, level = 1, order = []) => {
                const { anchorLevel = 1, anchorRange, anchorShowOrder = false } = meta;
                if (parseInt(anchorLevel) >= level) {
                    const childs = containers.filter(v => /^collapsecontainer$/.test(v.cControlType));
                    if (childs &&
                        childs.length) {
                        return (<ul className={['anchor-nav', `level-${level}`].join(' ')}>
                            {
                                childs.map((child, index) =>
                                    <li key={child.cGroupCode}>
                                        <a href={`#${child.cGroupCode}`} title={child.label} style={{ paddingLeft: anchorRange == 'tree' ? level * 20 : 20 }}>
                                            {
                                                anchorShowOrder &&
                                                    <span>{order.length > 0 ? `${order.join('.')}.${index+1}.` : `${index+1}.`}</span>
                                            }
                                            { child.label }
                                        </a>
                                        {anchorTreeLoop(child, level + 1, JSON.parse(JSON.stringify([...order, index + 1])))}
                                    </li>
                                )
                            }
                        </ul>);
                    }
                }
            };

            switch (anchorHorizontalAlign) {
                case 'right':
                    fixedAnchorStyle['right'] = 0;
                break;
                case 'left':
                    fixedAnchorStyle['right'] = 0;
                    fixedAnchorStyle['width'] = anchorContainerWidth;
                break;
                case 'center':
                default:
                    fixedAnchorStyle['right'] = Math.floor((anchorContainerWidth - clientWidth) / 2)
                break;
            }
        }

        return (
            <div
                id={cGroupCode}
                nid={nid}
                uitype={uitype}
                className={[ 'collapse-container', (this.isTop? 'isTop':''), (anchor? 'anchor':''), ...this.gitCustormClass('className')].join(' ')}
                style={{ ...this.gitCustormStyle('style') }}>

                {/* right anchor */}
                {isHasAnchor &&
                    <div className="right" style={{ width: `${anchorContainerWidth}px` }}>
                        <div id="anchor-container" style={ fixedAnchorStyle }>
                            { anchorTreeLoop(meta) }
                        </div>
                    </div>
                }
                {/* left components */}
                <div className={['nodes', (!label ? 'notitle' : ''), (isHasAnchor ? 'left' : '')].join(' ')} style={{ paddingRight: `${anchorContainerWidth}px` }}>
                    {/* title */}
                    {label &&
                        <div className={['title'].join(' ')}>
                            <h6
                                className={[(!this.state.collapse && 'open' || '')].join(' ')}
                                onClick={() => this.setState({ collapse: !this.state.collapse })}>
                                { label }
                                { components.length > 0 &&
                                    <SvgIcon type="right"/> }
                            </h6>
                        </div>
                    }

                    {/* content */}
                    {components.length > 0 &&
                        <Collapse
                            in={!this.state.collapse}
                            className={[ 'content', ...this.gitCustormClass('contentClassName') ].join(' ')}
                            style={{ ...this.gitCustormStyle('contentStyle') }}>
                            <div>
                                { components }
                            </div>
                        </Collapse>
                    }
                </div>
            </div>
        );
    }
}

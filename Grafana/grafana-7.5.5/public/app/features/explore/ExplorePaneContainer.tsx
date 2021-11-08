import React from 'react';
import { hot } from 'react-hot-loader';
import { compose } from 'redux';
import { connect, ConnectedProps } from 'react-redux';
import memoizeOne from 'memoize-one';
import { withTheme } from '@grafana/ui';
import { DataQuery, ExploreUrlState, EventBusExtended, EventBusSrv } from '@grafana/data';
import { selectors } from '@grafana/e2e-selectors';
import store from 'app/core/store';
import { lastSavedUrl, cleanupPaneAction } from './state/main';
import { initializeExplore, refreshExplore } from './state/explorePane';
import { ExploreId } from 'app/types/explore';
import { StoreState } from 'app/types';
import {
  DEFAULT_RANGE,
  ensureQueries,
  getTimeRange,
  getTimeRangeFromUrl,
  lastUsedDatasourceKeyForOrgId,
  parseUrlState,
} from 'app/core/utils/explore';
import { getTimeZone } from '../profile/state/selectors';
import Explore from './Explore';

type PropsFromRedux = ConnectedProps<typeof connector>;
interface Props extends PropsFromRedux {
  exploreId: ExploreId;
  split: boolean;
}

/**
 * This component is responsible for handling initialization of an Explore pane and triggering synchronization
 * of state based on URL changes and preventing any infinite loops.
 */
export class ExplorePaneContainerUnconnected extends React.PureComponent<Props & ConnectedProps<typeof connector>> {
  el: any;
  exploreEvents: EventBusExtended;

  constructor(props: Props) {
    super(props);
    this.exploreEvents = new EventBusSrv();
    this.state = {
      openDrawer: undefined,
    };
  }

  componentDidMount() {
    const { initialized, exploreId, initialDatasource, initialQueries, initialRange, originPanelId } = this.props;
    const width = this.el?.offsetWidth ?? 0;

    // initialize the whole explore first time we mount and if browser history contains a change in datasource
    if (!initialized) {
      this.props.initializeExplore(
        exploreId,
        initialDatasource,
        initialQueries,
        initialRange,
        width,
        this.exploreEvents,
        originPanelId
      );
    }
  }

  componentWillUnmount() {
    const { path } = this.props;
    this.exploreEvents.removeAllListeners();

    // We run cleanup only if we are still in explore and we are just closing single pane.
    // When navigating out of explore parent does the cleanup.
    if (path.match(/\/explore$/)) {
      this.props.cleanupPaneAction({ exploreId: this.props.exploreId });
    }
  }

  componentDidUpdate(prevProps: Props) {
    this.refreshExplore(prevProps.urlQuery);
  }

  refreshExplore = (prevUrlQuery: string) => {
    const { exploreId, urlQuery, path } = this.props;

    // Make sure we don't refresh after navigating outside as this would be called before parent unmounts
    if (!path.match(/\/explore$/)) {
      return;
    }

    // Update state from url only if it changed and only if the change wasn't initialised by redux to prevent any loops
    if (urlQuery !== prevUrlQuery && urlQuery !== lastSavedUrl[exploreId]) {
      this.props.refreshExplore(exploreId, urlQuery);
    }
  };

  getRef = (el: any) => {
    this.el = el;
  };

  render() {
    const exploreClass = this.props.split ? 'explore explore-split' : 'explore';
    return (
      <div className={exploreClass} ref={this.getRef} aria-label={selectors.pages.Explore.General.container}>
        {this.props.initialized && <Explore exploreId={this.props.exploreId} />}
      </div>
    );
  }
}

const ensureQueriesMemoized = memoizeOne(ensureQueries);
const getTimeRangeFromUrlMemoized = memoizeOne(getTimeRangeFromUrl);

function mapStateToProps(state: StoreState, { exploreId }: { exploreId: ExploreId }) {
  const urlQuery = state.location.query[exploreId] as string;
  const path = state.location.path;
  const urlState = parseUrlState(urlQuery);
  const timeZone = getTimeZone(state.user);

  const { datasource, queries, range: urlRange, originPanelId } = (urlState || {}) as ExploreUrlState;
  const initialDatasource = datasource || store.get(lastUsedDatasourceKeyForOrgId(state.user.orgId));
  const initialQueries: DataQuery[] = ensureQueriesMemoized(queries);
  const initialRange = urlRange
    ? getTimeRangeFromUrlMemoized(urlRange, timeZone)
    : getTimeRange(timeZone, DEFAULT_RANGE);

  return {
    initialized: state.explore[exploreId]?.initialized,
    initialDatasource,
    initialQueries,
    initialRange,
    originPanelId,
    urlQuery,
    path,
  };
}

const mapDispatchToProps = {
  initializeExplore,
  refreshExplore,
  cleanupPaneAction,
};

const connector = connect(mapStateToProps, mapDispatchToProps);

export const ExplorePaneContainer = compose(hot(module), connector, withTheme)(ExplorePaneContainerUnconnected);

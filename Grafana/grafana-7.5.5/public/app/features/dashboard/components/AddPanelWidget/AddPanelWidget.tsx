import React, { useMemo, useState } from 'react';
import { connect, MapDispatchToProps } from 'react-redux';
import { css, cx, keyframes } from 'emotion';
import _ from 'lodash';
import tinycolor from 'tinycolor2';
import { LocationUpdate } from '@grafana/runtime';
import { Icon, IconButton, styleMixins, useStyles } from '@grafana/ui';
import { selectors } from '@grafana/e2e-selectors';
import { DateTimeInput, GrafanaTheme } from '@grafana/data';

import config from 'app/core/config';
import store from 'app/core/store';
import { updateLocation } from 'app/core/actions';
import { addPanel } from 'app/features/dashboard/state/reducers';
import { DashboardModel, PanelModel } from '../../state';
import { LibraryPanelsView } from '../../../library-panels/components/LibraryPanelsView/LibraryPanelsView';
import { LS_PANEL_COPY_KEY } from 'app/core/constants';
import { LibraryPanelDTO } from '../../../library-panels/types';
import { toPanelModelLibraryPanel } from '../../../library-panels/utils';

export type PanelPluginInfo = { id: any; defaults: { gridPos: { w: any; h: any }; title: any } };

export interface OwnProps {
  panel: PanelModel;
  dashboard: DashboardModel;
}

export interface DispatchProps {
  addPanel: typeof addPanel;
  updateLocation: typeof updateLocation;
}

export type Props = OwnProps & DispatchProps;

const getCopiedPanelPlugins = () => {
  const panels = _.chain(config.panels)
    .filter({ hideFromList: false })
    .map((item) => item)
    .value();
  const copiedPanels = [];

  const copiedPanelJson = store.get(LS_PANEL_COPY_KEY);
  if (copiedPanelJson) {
    const copiedPanel = JSON.parse(copiedPanelJson);
    const pluginInfo: any = _.find(panels, { id: copiedPanel.type });
    if (pluginInfo) {
      const pluginCopy = _.cloneDeep(pluginInfo);
      pluginCopy.name = copiedPanel.title;
      pluginCopy.sort = -1;
      pluginCopy.defaults = copiedPanel;
      copiedPanels.push(pluginCopy);
    }
  }

  return _.sortBy(copiedPanels, 'sort');
};

export const AddPanelWidgetUnconnected: React.FC<Props> = ({ panel, dashboard, updateLocation }) => {
  const [addPanelView, setAddPanelView] = useState(false);

  const onCancelAddPanel = (evt: React.MouseEvent<HTMLButtonElement>) => {
    evt.preventDefault();
    dashboard.removePanel(panel);
  };

  const onBack = () => {
    setAddPanelView(false);
  };

  const onCreateNewPanel = () => {
    const { gridPos } = panel;

    const newPanel: Partial<PanelModel> = {
      type: 'graph',
      title: 'Panel Title',
      gridPos: { x: gridPos.x, y: gridPos.y, w: gridPos.w, h: gridPos.h },
    };

    dashboard.addPanel(newPanel);
    dashboard.removePanel(panel);

    const location: LocationUpdate = {
      query: {
        editPanel: newPanel.id,
      },
      partial: true,
    };

    updateLocation(location);
  };

  const onPasteCopiedPanel = (panelPluginInfo: PanelPluginInfo) => {
    const { gridPos } = panel;

    const newPanel: any = {
      type: panelPluginInfo.id,
      title: 'Panel Title',
      gridPos: {
        x: gridPos.x,
        y: gridPos.y,
        w: panelPluginInfo.defaults.gridPos.w,
        h: panelPluginInfo.defaults.gridPos.h,
      },
    };

    // apply panel template / defaults
    if (panelPluginInfo.defaults) {
      _.defaults(newPanel, panelPluginInfo.defaults);
      newPanel.title = panelPluginInfo.defaults.title;
      store.delete(LS_PANEL_COPY_KEY);
    }

    dashboard.addPanel(newPanel);
    dashboard.removePanel(panel);
  };

  const onAddLibraryPanel = (panelInfo: LibraryPanelDTO) => {
    const { gridPos } = panel;

    const newPanel: PanelModel = {
      ...panelInfo.model,
      gridPos,
      libraryPanel: toPanelModelLibraryPanel(panelInfo),
    };

    dashboard.addPanel(newPanel);
    dashboard.removePanel(panel);
  };

  const onCreateNewRow = () => {
    const newRow: any = {
      type: 'row',
      title: 'Row title',
      gridPos: { x: 0, y: 0 },
    };

    dashboard.addPanel(newRow);
    dashboard.removePanel(panel);
  };

  const styles = useStyles(getStyles);
  const copiedPanelPlugins = useMemo(() => getCopiedPanelPlugins(), []);

  return (
    <div className={cx('panel-container', styles.wrapper)}>
      <AddPanelWidgetHandle onCancel={onCancelAddPanel} onBack={addPanelView ? onBack : undefined} styles={styles}>
        {addPanelView ? 'Add panel from panel library' : 'Add panel'}
      </AddPanelWidgetHandle>
      {addPanelView ? (
        <LibraryPanelsView
          className={styles.libraryPanelsWrapper}
          formatDate={(dateString: DateTimeInput) => dashboard.formatDate(dateString, 'L')}
          onClickCard={(panel) => onAddLibraryPanel(panel)}
          showSecondaryActions={false}
        />
      ) : (
        <div className={styles.actionsWrapper}>
          <div className={styles.actionsRow}>
            <div onClick={() => onCreateNewPanel()} aria-label={selectors.pages.AddDashboard.addNewPanel}>
              <Icon name="file-blank" size="xl" />
              Add an empty panel
            </div>
            <div onClick={onCreateNewRow}>
              <Icon name="wrap-text" size="xl" />
              Add a new row
            </div>
          </div>
          {(config.featureToggles.panelLibrary || copiedPanelPlugins.length === 1) && (
            <div className={styles.actionsRow}>
              {config.featureToggles.panelLibrary && (
                <div onClick={() => setAddPanelView(true)}>
                  <Icon name="book-open" size="xl" />
                  Add a panel from the panel library
                </div>
              )}
              {copiedPanelPlugins.length === 1 && (
                <div onClick={() => onPasteCopiedPanel(copiedPanelPlugins[0])}>
                  <Icon name="clipboard-alt" size="xl" />
                  Paste panel from clipboard
                </div>
              )}
            </div>
          )}
        </div>
      )}
    </div>
  );
};

const mapDispatchToProps: MapDispatchToProps<DispatchProps, OwnProps> = { addPanel, updateLocation };

export const AddPanelWidget = connect(undefined, mapDispatchToProps)(AddPanelWidgetUnconnected);

interface AddPanelWidgetHandleProps {
  onCancel: (e: React.MouseEvent<HTMLButtonElement>) => void;
  onBack?: () => void;
  children?: string;
  styles: AddPanelStyles;
}

const AddPanelWidgetHandle: React.FC<AddPanelWidgetHandleProps> = ({ children, onBack, onCancel, styles }) => {
  return (
    <div className={cx(styles.headerRow, 'grid-drag-handle')}>
      {onBack && (
        <div className={styles.backButton}>
          <IconButton name="arrow-left" onClick={onBack} surface="header" size="xl" />
        </div>
      )}
      {!onBack && (
        <div className={styles.backButton}>
          <Icon name="panel-add" size="md" />
        </div>
      )}
      {children && <span>{children}</span>}
      <div className="flex-grow-1" />
      <IconButton name="times" onClick={onCancel} surface="header" />
    </div>
  );
};

const getStyles = (theme: GrafanaTheme) => {
  const pulsate = keyframes`
    0% {box-shadow: 0 0 0 2px ${theme.colors.bodyBg}, 0 0 0px 4px ${theme.colors.formFocusOutline};}
    50% {box-shadow: 0 0 0 2px ${theme.colors.bodyBg}, 0 0 0px 4px ${tinycolor(theme.colors.formFocusOutline)
    .darken(20)
    .toHexString()};}
    100% {box-shadow: 0 0 0 2px ${theme.colors.bodyBg}, 0 0 0px 4px  ${theme.colors.formFocusOutline};}
  `;

  return {
    wrapper: css`
      overflow: hidden;
      outline: 2px dotted transparent;
      outline-offset: 2px;
      box-shadow: 0 0 0 2px black, 0 0 0px 4px #1f60c4;
      animation: ${pulsate} 2s ease infinite;
    `,
    actionsRow: css`
      display: flex;
      flex-direction: row;
      column-gap: ${theme.spacing.sm};
      height: 100%;

      > div {
        justify-self: center;
        cursor: pointer;
        background: ${theme.colors.bg2};
        border-radius: ${theme.border.radius.sm};
        color: ${theme.colors.text};
        width: 100%;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        text-align: center;

        &:hover {
          background: ${styleMixins.hoverColor(theme.colors.bg2, theme)};
        }

        &:hover > #book-icon {
          background: linear-gradient(#f05a28 30%, #fbca0a 99%);
        }
      }
    `,
    actionsWrapper: css`
      display: flex;
      flex-direction: column;
      row-gap: ${theme.spacing.sm};
      padding: 0 ${theme.spacing.sm} ${theme.spacing.sm} ${theme.spacing.sm};
      height: 100%;
    `,
    libraryPanelsWrapper: css`
      padding: ${theme.spacing.sm};
    `,
    headerRow: css`
      display: flex;
      align-items: center;
      height: 38px;
      flex-shrink: 0;
      width: 100%;
      font-size: ${theme.typography.size.md};
      font-weight: ${theme.typography.weight.semibold};
      padding-left: ${theme.spacing.sm};
      transition: background-color 0.1s ease-in-out;
      cursor: move;

      &:hover {
        background: ${theme.colors.bg2};
      }
    `,
    backButton: css`
      display: flex;
      align-items: center;
      cursor: pointer;
      padding-left: ${theme.spacing.xs};
      width: ${theme.spacing.xl};
    `,
    noMargin: css`
      margin: 0;
    `,
  };
};

type AddPanelStyles = ReturnType<typeof getStyles>;

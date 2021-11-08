import React from 'react';
import { LogRows, CustomScrollbar } from '@grafana/ui';
import { PanelProps } from '@grafana/data';
import { Options } from './types';
import { dataFrameToLogsModel, dedupLogRows } from 'app/core/logs_model';

interface LogsPanelProps extends PanelProps<Options> {}

export const LogsPanel: React.FunctionComponent<LogsPanelProps> = ({
  data,
  timeZone,
  options: { showLabels, showTime, wrapLogMessage, sortOrder, dedupStrategy },
}) => {
  if (!data) {
    return (
      <div className="panel-empty">
        <p>No data found in response</p>
      </div>
    );
  }

  const newResults = data ? dataFrameToLogsModel(data.series, data.request?.intervalMs, timeZone) : null;
  const logRows = newResults?.rows || [];
  const deduplicatedRows = dedupLogRows(logRows, dedupStrategy);

  return (
    <CustomScrollbar autoHide>
      <LogRows
        logRows={logRows}
        deduplicatedRows={deduplicatedRows}
        dedupStrategy={dedupStrategy}
        highlighterExpressions={[]}
        showLabels={showLabels}
        showTime={showTime}
        wrapLogMessage={wrapLogMessage}
        timeZone={timeZone}
        allowDetails={true}
        disableCustomHorizontalScroll={true}
        logsSortOrder={sortOrder}
      />
    </CustomScrollbar>
  );
};

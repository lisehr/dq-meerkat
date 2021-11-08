import { DataSourceInstanceSettings } from '@grafana/data';
import { backendSrv } from 'app/core/services/backend_srv';
import { of } from 'rxjs';
import { createFetchResponse } from 'test/helpers/createFetchResponse';
import { ZipkinDatasource } from './datasource';
import { jaegerTrace, zipkinResponse } from './utils/testData';

jest.mock('@grafana/runtime', () => ({
  ...((jest.requireActual('@grafana/runtime') as unknown) as object),
  getBackendSrv: () => backendSrv,
}));

describe('ZipkinDatasource', () => {
  describe('query', () => {
    it('runs query', async () => {
      setupBackendSrv(zipkinResponse);
      const ds = new ZipkinDatasource(defaultSettings);
      await expect(ds.query({ targets: [{ query: '12345' }] } as any)).toEmitValuesWith((val) => {
        expect(val[0].data[0].fields[0].values.get(0)).toEqual(jaegerTrace);
      });
    });
    it('runs query with traceId that includes special characters', async () => {
      setupBackendSrv(zipkinResponse);
      const ds = new ZipkinDatasource(defaultSettings);
      await expect(ds.query({ targets: [{ query: 'a/b' }] } as any)).toEmitValuesWith((val) => {
        expect(val[0].data[0].fields[0].values.get(0)).toEqual(jaegerTrace);
      });
    });
  });

  describe('metadataRequest', () => {
    it('runs query', async () => {
      setupBackendSrv(['service 1', 'service 2']);
      const ds = new ZipkinDatasource(defaultSettings);
      const response = await ds.metadataRequest('/api/v2/services');
      expect(response).toEqual(['service 1', 'service 2']);
    });
  });
});

function setupBackendSrv(response: any) {
  const defaultMock = () => of(createFetchResponse(response));

  const fetchMock = jest.spyOn(backendSrv, 'fetch');
  fetchMock.mockImplementation(defaultMock);
}

const defaultSettings: DataSourceInstanceSettings = {
  id: 1,
  uid: '1',
  type: 'tracing',
  name: 'zipkin',
  meta: {} as any,
  jsonData: {},
};

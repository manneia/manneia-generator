import { listGeneratorVoByPageUsingPost } from '@/services/backend/generatorController';
import { UserOutlined } from '@ant-design/icons';
import { PageContainer, ProFormSelect, ProFormText, QueryFilter } from '@ant-design/pro-components';
import { Avatar, Card, Flex, Image, Input, List, message, Tabs, Tag, Typography } from 'antd';
import moment from 'moment';
import React, { useEffect, useState } from 'react';

/**
 * 默认分页参数
 */
const DEFAULT_PAGE_PARAMS: PageRequest = {
  current: 1,
  pageSize: 4,
  sortField: 'createTime',
  sortOrder: 'descend',
};

const IndexPage: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(true);
  const [dataList, setDataList] = useState<API.GeneratorVo[]>([]);
  const [total, setTotal] = useState<number>(0);
  // 搜索条件
  const [searchParams, setSearchParams] = useState<API.GeneratorQueryRequest>({
    ...DEFAULT_PAGE_PARAMS,
  });

  /**
   * 搜索数据
   */
  const doSearch = async () => {
    setLoading(true);
    try {
      const res = await listGeneratorVoByPageUsingPost(searchParams);
      setDataList(res.data?.records ?? []);
      setTotal(res.data?.total ?? 0);
    } catch (e: any) {
      message.error('获取数据失败', e.message);
    }
    setLoading(false);
  };
  useEffect(() => {
    doSearch().then((r) => console.log(r));
  }, [searchParams]);

  /**
   * 标签列表
   * @param tags 标签数组
   */
  const tagListView = (tags?: string[]) => {
    if (!tags) {
      return <></>;
    }
    return (
      <div style={{ marginBottom: 8 }}>
        {tags.map((tag) => (
          <Tag key={tag}>{tag}</Tag>
        ))}
      </div>
    );
  };
  return (
    <PageContainer title={<></>}>
      <Flex justify="center">
        <Input.Search
          placeholder="请输入搜索词"
          allowClear
          enterButton="搜索"
          size="large"
          style={{ width: '40vw', minWidth: 320 }}
          onChange={(e) => {
            searchParams.searchText = e.target.value;
          }}
          onSearch={(value: string) => {
            setSearchParams({
              ...DEFAULT_PAGE_PARAMS,
              searchText: value,
            });
          }}
        />
      </Flex>
      <div style={{ marginBottom: 16 }}></div>
      <Tabs
        size="large"
        defaultActiveKey="newest"
        items={[
          {
            key: 'newest',
            label: '最新',
          },
          {
            key: 'recommend',
            label: '推荐',
          },
        ]}
        onChange={() => {}}
      />
      <QueryFilter
        span={12}
        labelWidth="auto"
        labelAlign="left"
        style={{ padding: '16px, 0' }}
        onFinish={async (values: API.GeneratorQueryRequest) => {
          setSearchParams({
            ...DEFAULT_PAGE_PARAMS,
            ...values,
            searchText: searchParams.searchText,
          });
        }}
      >
        <ProFormSelect label="标签" name="tags" mode="tags" />
        <ProFormText label="名称" name="name" />
        <ProFormText label="描述" name="description" />
      </QueryFilter>
      <div style={{ marginBottom: 24 }} />
      <List<API.GeneratorVo>
        rowKey="id"
        loading={loading}
        grid={{
          gutter: 16,
          xs: 1,
          sm: 2,
          md: 3,
          lg: 3,
          xl: 4,
          xxl: 4,
        }}
        dataSource={dataList}
        pagination={{
          current: searchParams.current,
          pageSize: searchParams.pageSize,
          total,
          onChange(current: number, pageSize: number) {
            setSearchParams({
              ...searchParams,
              current,
              pageSize,
            });
          },
        }}
        renderItem={(data) => (
          <List.Item>
            <Card hoverable cover={<Image alt={data.name} src={data.picture} />}>
              <Card.Meta
                title={<a>{data.name}</a>}
                description={
                  <Typography.Paragraph
                    ellipsis={{
                      rows: 2,
                    }}
                    style={{ height: 44 }}
                  >
                    {data.description}
                  </Typography.Paragraph>
                }
              />
              {tagListView(data.tags ?? [])}
              <Flex justify="space-between" align="center">
                <Typography.Paragraph type="secondary" style={{ fontSize: 12 }}>
                  {moment(data.createTime).fromNow()}
                </Typography.Paragraph>
                <div>
                  {/*@ts-ignore*/}
                  <Avatar src={data.user?.userAvatar ?? UserOutlined}></Avatar>
                </div>
              </Flex>
            </Card>
          </List.Item>
        )}
      />
    </PageContainer>
  );
};
export default IndexPage;

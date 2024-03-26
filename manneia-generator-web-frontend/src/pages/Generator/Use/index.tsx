import {
  getGeneratorVoByIdUsingGet,
  useGeneratorUsingPost,
} from '@/services/backend/generatorController';
import { Link, useModel, useParams } from '@@/exports';
import { DownloadOutlined } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-components';
import {
  Button,
  Card,
  Col,
  Collapse,
  Form,
  Image,
  Input,
  message,
  Row,
  Space,
  Tag,
  Typography,
} from 'antd';
import { saveAs } from 'file-saver';
import React, { useEffect, useState } from 'react';

/**
 * 生成器使用页面
 * @constructor
 */
const GeneratorUsePage: React.FC = () => {
  const { id } = useParams();
  const [loading, setLoading] = useState<boolean>(true);
  const [downloading, setDownLoading] = useState<boolean>(false);
  const [data, setData] = useState<API.GeneratorVo>({});
  const [form] = Form.useForm();
  const loadData = async () => {
    if (!id) {
      return;
    }
    setLoading(true);
    try {
      // @ts-ignore
      const res = await getGeneratorVoByIdUsingGet({ id });
      setData(res.data ?? {});
    } catch (e: any) {
      message.error('获取数据失败' + e.message);
    }
    setLoading(false);
  };
  useEffect(() => {
    if (id) {
      loadData().then(() => {});
    }
  }, [id]);

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
  const { initialState } = useModel('@@initialState');
  const { currentUser } = initialState ?? {};
  const models = data?.modelConfig?.models ?? [];

  /**
   * 下载按钮
   */
  const downloadButton = data.distPath && currentUser && (
    <Button
      type="primary"
      //@ts-ignore
      icon={<DownloadOutlined />}
      loading={downloading}
      onClick={async () => {
        setDownLoading(true);
        const values = form.getFieldsValue();
        // eslint-disable-next-line react-hooks/rules-of-hooks
        const blob = await useGeneratorUsingPost(
          {
            id: data.id,
            dataModel: values,
          },
          {
            responseType: 'blob',
          },
        );
        const fullPath = data.distPath || '';
        saveAs(blob, fullPath.substring(fullPath.lastIndexOf('/') + 1));
        setDownLoading(false);
      }}
    >
      生成代码
    </Button>
  );
  return (
    <PageContainer title={<></>} loading={loading}>
      <Card>
        <Row justify="space-between" gutter={[32, 32]}>
          <Col flex="auto">
            <Space size="large" align="center">
              <Typography.Title level={4}>{data.name}</Typography.Title>
              {tagListView(data.tags)}
            </Space>
            <Typography.Paragraph>{data.description}</Typography.Paragraph>
            <Form form={form}>
              {models.map((model: API.ModelInfo, index) => {
                // 是分组
                if (model.groupKey) {
                  if (!model.models) {
                    return <></>;
                  }
                  return (
                    <Collapse
                      key={index}
                      style={{ marginBottom: 24 }}
                      items={[
                        {
                          key: index,
                          label: model.groupName + '(分组)',
                          children: model.models?.map((subModel, index) => {
                            return (
                              <Form.Item
                                key={index}
                                label={subModel.fieldName}
                                // @ts-ignore
                                name={[model.groupKey, subModel.fieldName]}
                              >
                                  <Input placeholder={subModel.description} />
                              </Form.Item>
                            );
                          }),
                        },
                      ]}
                      bordered={false}
                      defaultActiveKey={[index]}
                      onChange={() => {}}
                    />
                  );
                }
                return (
                  // eslint-disable-next-line react/jsx-key
                  <Form.Item label={model.fieldName} name={model.fieldName}>
                    <Input placeholder={model.description} />
                  </Form.Item>
                );
              })}
            </Form>
            <div style={{ marginBottom: 24 }} />
            <Space size="middle">
              {downloadButton}
              <Link to={`/generator/detail/${id}`}>
                <Button>查看详情</Button>
              </Link>
            </Space>
          </Col>
          <Col flex="320px">
            <Image src={data.picture} />
          </Col>
        </Row>
      </Card>
    </PageContainer>
  );
};

export default GeneratorUsePage;

import { Card } from 'antd';
import React from 'react';

interface Props {
  data: API.GeneratorVo;
}

/**
 * 作者信息
 * @param props
 * @constructor
 */
const AuthorInfoPage: React.FC<Props> = (props) => {
  const { data } = props;
  const user = data?.user;
  if (!user) {
    return <></>;
  }
  return (
    <div style={{ marginTop: 16 }}>
      <Card.Meta title={user.userName} description={user.userProfile} avatar={user.userAvatar} />
    </div>
  );
};

export default AuthorInfoPage;

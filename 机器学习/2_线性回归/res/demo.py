import tensorflow as tf
import numpy as np
import matplotlib.pyplot as plt
from matplotlib import cm
from mpl_toolkits.mplot3d import Axes3D

# 使用 NumPy 生成假数据(phony data), 总共 1000 个点.
dotCount = 1000
x_data = np.float32(np.random.rand(2, dotCount))
x_data_0 = x_data[0]
x_data_1 = x_data[1]

# 随机输入
y_data = []
for count in range(0, 1000):
    y_data_0 = ((1 + (np.random.rand(1) - 1) / 100) * x_data_0[count])[0]
    y_data_1 = ((2 + (np.random.rand(1) - 1) / 100) * x_data_1[count])[0]
    y_data.append(y_data_0 + y_data_1 + 3 + np.random.rand(1)[0] - 1)


# 构造一个线性模型
b = tf.Variable(tf.zeros([1]))
W = tf.Variable(tf.random_uniform([1, 2], -1.0, 1.0))
y = tf.matmul(W, x_data) + b

# 最小化方差
loss = tf.reduce_mean(tf.square(y - y_data))
optimizer = tf.train.GradientDescentOptimizer(0.01)
train = optimizer.minimize(loss)

# 初始化变量
init = tf.initialize_all_variables()

# 启动图 (graph)
sess = tf.Session()
sess.run(init)

# 定义坐标轴
fig = plt.figure()
ax = Axes3D(fig)

# 所有的点画图
ax.scatter3D(x_data_0, x_data_1, y_data, cmap='Blues') 

# 定义三维曲面范围
X = np.arange(0, 1, 0.1)
Y = np.arange(0, 1, 0.1)
X, Y = np.meshgrid(X, Y)

# 拟合第一个平面
sess.run(train)

# 画图第一个平面
result_0 = sess.run(W)
result_1 = sess.run(b)
Z = result_0[0][0] * X + result_0[0][1] * Y + result_1[0]
ax.plot_surface(X, Y, Z, rstride=1, cstride=1, cmap='Reds')

# 拟合平面
for step in range(0, 20001):
    sess.run(train)
    if step % 2000 == 0:
        print(step, sess.run(W), sess.run(b))

result_0 = sess.run(W)
result_1 = sess.run(b)
Z = result_0[0][0] * X + result_0[0][1] * Y + result_1[0]
ax.plot_surface(X, Y, Z, rstride=1, cstride=1, cmap=cm.viridis)

plt.show()
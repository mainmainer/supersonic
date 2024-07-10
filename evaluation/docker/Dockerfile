FROM python:3.7-slim

WORKDIR /work

COPY . /work

RUN pip install --no-cache-dir -r requirements.txt

# 如果不能自动运行请尝试加上：RUN chmod +x main.py

CMD ["python", "main.py"]

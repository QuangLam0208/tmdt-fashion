
import React, { useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import '../../styles/about.css';

const IconFabric = () => (
  <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="#c9a96e" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round">
    <path d="M6 2L3 6v14a2 2 0 002 2h14a2 2 0 002-2V6l-3-4z" />
    <line x1="3" y1="6" x2="21" y2="6" />
    <path d="M16 10a4 4 0 01-8 0" />
  </svg>
);

const IconTrend = () => (
  <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="#c9a96e" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round">
    <polyline points="23 6 13.5 15.5 8.5 10.5 1 18" />
    <polyline points="17 6 23 6 23 12" />
  </svg>
);

const IconStar = () => (
  <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="#c9a96e" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round">
    <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2" />
  </svg>
);

const IconLeaf = () => (
  <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="#c9a96e" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round">
    <path d="M17 8C8 10 5.9 16.17 3.82 19.34" />
    <path d="M2 22c1-4 4-8 8.5-9.5S21 10 21 3c-4 0-9 3-11 7" />
  </svg>
);

// ────────────────────────────────────────────
// DATA — Core Values
// ────────────────────────────────────────────
const coreValues = [
  {
    id: 1,
    Icon: IconFabric,
    title: 'Chất Liệu Tuyển Chọn',
    desc: 'Mỗi sản phẩm được lựa chọn từ nguồn vải cao cấp, đảm bảo độ bền vượt thời gian và cảm giác thoải mái tuyệt đối khi mặc trong mọi hoàn cảnh.',
  },
  {
    id: 2,
    Icon: IconTrend,
    title: 'Thiết Kế Xu Hướng',
    desc: 'Đội ngũ sáng tạo liên tục cập nhật xu hướng thời trang quốc tế, mang đến những mẫu thiết kế phù hợp vóc dáng người Việt và cá tính streetwear hiện đại.',
  },
  {
    id: 3,
    Icon: IconStar,
    title: 'Trải Nghiệm Thông Minh',
    desc: 'Đổi size miễn phí trong 7 ngày, tư vấn phối đồ 1:1 tận tâm. Chúng tôi đồng hành cùng bạn từ lúc chọn size đến khi bạn tự tin bước ra đường.',
  },
  {
    id: 4,
    Icon: IconLeaf,
    title: 'Phát Triển Bền Vững',
    desc: 'Chúng tôi tin vào triết lý "ít hơn nhưng tốt hơn": tập trung vào chất lượng lâu dài, hạn chế rác thải thời trang và hướng đến một tủ quần áo có ý thức hơn.',
  },
];

// ────────────────────────────────────────────
// DATA — Stats
// ────────────────────────────────────────────
const stats = [
  { num: '15.000+', label: 'Khách Hàng Hài Lòng' },
  { num: '50+',     label: 'BST Đã Ra Mắt' },
  { num: '3+',      label: 'Năm Hoạt Động' },
  { num: '100%',    label: 'Tự Thiết Kế & Tuyển Chọn' },
];

// ════════════════════════════════════════════
// MAIN COMPONENT
// ════════════════════════════════════════════
const AboutPage = () => {
  const navigate    = useNavigate();
  const pageRef     = useRef(null);
  const observerRef = useRef(null);

  // ── Intersection Observer — Scroll Reveal ──
  // Dùng className thay inline style để tránh specificity conflict
  useEffect(() => {
    const container = pageRef.current;
    if (!container) return;

    const targets = container.querySelectorAll('.about-reveal, .about-reveal--left, .about-reveal--right, .about-reveal--scale');

    observerRef.current = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            // Lấy delay từ data attribute (ms)
            const delay = parseInt(entry.target.dataset.delay || '0', 10);
            setTimeout(() => {
              entry.target.classList.add('is-visible');
            }, delay);
            observerRef.current.unobserve(entry.target);
          }
        });
      },
      { threshold: 0.08, rootMargin: '0px 0px -40px 0px' }
    );

    targets.forEach((el) => observerRef.current.observe(el));

    return () => observerRef.current?.disconnect();
  }, []);

  return (
    <div className="about-page" ref={pageRef}>

      {/* ═══════════════════════════════════════
          1. HERO SECTION
          — Không dùng scroll-reveal, dùng CSS animation trực tiếp
      ═══════════════════════════════════════ */}
      <section className="about-hero" aria-label="Giới thiệu Store">

        {/* Left — Text Content */}
        <div className="about-hero__left">
          <span className="about-hero__eyebrow"></span>

          <h1 className="about-hero__title">
            Định Hình{' '}
            <span className="highlight">Phong Cách</span>
            {' '}—{' '}<br />
            Khẳng Định{' '}
            <span className="highlight">Bản Sắc</span>
          </h1>

          <p className="about-hero__sub">
            Không chỉ là quần áo — chúng tôi kiến tạo ngôn ngữ thời trang
            cho thế hệ trẻ dám sống thật với chính mình. Mỗi item là một
            tuyên ngôn cá tính.
          </p>

          <div className="about-hero__cta">
            <button
              className="about-btn-primary"
              onClick={() => navigate('/shop')}
              id="about-hero-shop-btn"
            >
              Khám Phá BST
            </button>
            <span className="about-hero__scroll-hint">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#555" strokeWidth="2" strokeLinecap="round">
                <line x1="12" y1="5" x2="12" y2="19" />
                <polyline points="19 12 12 19 5 12" />
              </svg>
              Cuộn để khám phá
            </span>
          </div>
        </div>

        {/* Right — Image */}
        <div className="about-hero__right">
          <img
            src="https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=900&q=85&auto=format&fit=crop"
            alt=" Phong cách thời trang hiện đại"
            className="about-hero__img"
          />
          <div className="about-hero__overlay" aria-hidden="true" />

          {/* Floating badge */}
          <div className="about-hero__badge" aria-label="Thống kê nổi bật">
            <div className="about-hero__badge-num">15K+</div>
            <div className="about-hero__badge-label">Khách hàng tin tưởng</div>
          </div>
        </div>
      </section>

      {/* ═══════════════════════════════════════
          2. OUR STORY SECTION
      ═══════════════════════════════════════ */}
      <section className="about-story" aria-label="Câu chuyện thương hiệu">
        <div className="about-story__grid">

          {/* Left — Text */}
          <div className="about-reveal--left" data-delay="0">
            <p className="about-section-eyebrow">Our Story</p>
            <h2 className="about-section-title">
              Bắt Đầu Từ Một Câu Hỏi
              <span className="gold-line" />
            </h2>
            <div className="about-story__body">
              <p>
                Năm 2021, nhóm tự hỏi:{' '}
                <strong>Tại sao giới trẻ Việt phải chọn giữa "đẹp" và "túi tiền"?</strong>{' '}
                Thị trường tràn ngập hàng kém chất lượng hoặc những thương hiệu ngoại đắt đỏ không phù hợp vóc dáng người Việt.
              </p>
              <p>
                Chúng tôi bắt đầu từ một studio nhỏ, một đội ngũ đam mê và cam kết rõ ràng:{' '}
                <strong>mang thiết kế hiện đại, chất liệu được tuyển chọn kỹ lưỡng đến tay khách hàng với mức giá thật sự xứng đáng</strong>{' '}
                — không thổi phồng, không đánh đổi chất lượng.
              </p>
              <p>
                Mỗi bộ sưu tập ra mắt đều mang theo một câu chuyện, một cảm xúc.
                Chúng tôi không chỉ bán quần áo — chúng tôi đồng hành cùng bạn trong{' '}
                <strong>hành trình tìm và sống với phong cách của riêng mình</strong>.
              </p>
            </div>
          </div>

          {/* Right — Image */}
          <div className="about-story__img-wrapper about-reveal--right" data-delay="150">
            <img
              src="https://images.unsplash.com/photo-1558769132-cb1aea458c5e?w=800&q=85&auto=format&fit=crop"
              alt="Phòng studio lookbook tối giản"
              className="about-story__img"
            />
            <p className="about-story__img-caption">
              Studio sáng tạo — nơi mỗi BST được thai nghén
            </p>
          </div>
        </div>
      </section>

      {/* ═══════════════════════════════════════
          3. CORE VALUES SECTION
      ═══════════════════════════════════════ */}
      <section className="about-values" aria-label="Giá trị cốt lõi">

        {/* Header */}
        <div className="about-values__header about-reveal" data-delay="0">
          <p className="about-section-eyebrow" style={{ textAlign: 'center' }}>Core Values</p>
          <h2 className="about-section-title" style={{ textAlign: 'center' }}>
            Điều Chúng Tôi Đứng Vững
          </h2>
          <p className="about-values__sub">
            Bốn trụ cột định hình mọi quyết định — từ chọn chất liệu, thiết kế
            sản phẩm đến cách chúng tôi phục vụ từng khách hàng.
          </p>
        </div>

        {/* Grid 4 cột */}
        <div className="about-values__grid">
          {coreValues.map((v, i) => (
            <div
              key={v.id}
              className="about-value-card about-reveal"
              data-delay={i * 120}
            >
              <div className="about-value-card__icon">
                <v.Icon />
              </div>
              <h3 className="about-value-card__title">{v.title}</h3>
              <p className="about-value-card__desc">{v.desc}</p>
            </div>
          ))}
        </div>
      </section>

      {/* ═══════════════════════════════════════
          4. STATS COUNTER SECTION
      ═══════════════════════════════════════ */}
      <section className="about-stats" aria-label="Con số ấn tượng">
        <div className="about-stats__grid">
          {stats.map((s, i) => (
            <div
              key={s.label}
              className="about-stat-item about-reveal--scale"
              data-delay={i * 100}
            >
              <div className="about-stat-item__num">{s.num}</div>
              <div className="about-stat-item__label">{s.label}</div>
            </div>
          ))}
        </div>
      </section>

      {/* ═══════════════════════════════════════
          5. OUR PROMISE SECTION (Blockquote)
      ═══════════════════════════════════════ */}
      <section className="about-promise" aria-label="Lời cam kết từ Founder">
        <div className="about-promise__inner about-reveal" data-delay="0">
          <p className="about-section-eyebrow" style={{ textAlign: 'center', marginBottom: '32px' }}>
            Our Promise
          </p>

          {/* Dấu ngoặc mở */}
          <span className="about-promise__quote-icon" aria-hidden="true">"</span>

          <blockquote className="about-promise__blockquote">
            Chúng tôi không ngừng sáng tạo vì một lý do duy nhất — để mỗi buổi sáng
            bạn mở tủ quần áo, bạn tìm thấy{' '}
            <em>đúng thứ mình cần</em>,{' '}
            cảm thấy tự tin và sẵn sàng chinh phục bất cứ điều gì phía trước.
            Đó là lời hứa của chúng tôi với mỗi khách hàng.
          </blockquote>

          <div className="about-promise__divider" aria-hidden="true" />

          <div className="about-promise__author">
            <span className="about-promise__author-name">Nguyễn Minh Anh</span>
            <span className="about-promise__author-role">Co-Founder &amp; Creative Director</span>
          </div>
        </div>
      </section>

    </div>
  );
};

export default AboutPage;
